package com.nexos.api_inventory.service.impl;

import com.nexos.api_inventory.dto.productmovement.ProductMovementRequestDto;
import com.nexos.api_inventory.dto.productmovement.ProductMovementResponseDto;
import com.nexos.api_inventory.dto.user.UserResponseDto;
import com.nexos.api_inventory.entity.Product;
import com.nexos.api_inventory.entity.ProductMovement;
import com.nexos.api_inventory.enums.MovementTypes;
import com.nexos.api_inventory.mapper.ProductMovementMapper;
import com.nexos.api_inventory.repository.ProductMovementRepository;
import com.nexos.api_inventory.service.IProductMovementService;
import com.nexos.api_inventory.service.IProductService;
import com.nexos.api_inventory.service.IUserService;
import com.nexos.api_inventory.util.error.BaseError;
import com.nexos.api_inventory.util.error.ErrorUtil;
import com.nexos.api_inventory.util.jpafilter.JpaFilterHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductMovementService implements IProductMovementService {

    private static final String ERROR_PRODUCT_MOVEMENT_NOT_FOUND = "error.product_movement.not_found";
    private static final String ERROR_PRODUCT_MOVEMENT_DELETE_INVALID_USER = "error.product_movement.delete.invalid_user";
    private static final String ERROR_PRODUCT_MOVEMENT_DELETE_NOT_ACTIVE = "error.product_movement.delete.not_active";
    private static final String ERROR_PRODUCT_MOVEMENT_INSUFFICIENT_STOCK = "error.product_movement.insufficient_stock";
    private static final String ERROR_PRODUCT_MOVEMENT_INVALID_MOMENT_TYPE = "error.product_movement.invalid_movement_type";
    public static final String ERROR_STOCK_NEGATIVE_AFTER_REVERT = "error.product_movement.stock_negative.after_revert";

    private static final String DEFAULT_PRODUCT_CONFIG_FILTER = "product.id==";

    private final ProductMovementRepository productMovementRepository;
    private final IProductService productService;
    private final IUserService userService;
    private final JpaFilterHelper jpaFilterHelper;
    private final ErrorUtil errorUtil;

    protected static final ProductMovementMapper mapper = ProductMovementMapper.INSTANCE;

    @Override
    public Page<ProductMovementResponseDto> getAllByProduct(Long productId, Pageable pageable, String filters) throws BaseError {
        return getAllProductsByFilters(pageable, DEFAULT_PRODUCT_CONFIG_FILTER + productId, filters)
                .map(mapper::toDto);
    }

    @Override
    public ProductMovementResponseDto getOne(Long productId, Long id) throws BaseError {
        return mapper.toDto(getByIdAndProductId(id, productId));
    }

    private Page<ProductMovement> getAllProductsByFilters(Pageable pageable, String... filters) {
        return productMovementRepository.findAll(jpaFilterHelper.doOnEvery(Specification::and, filters),
                pageable
        );
    }

    @Override
    @Transactional
    public ProductMovementResponseDto create(Long productId, ProductMovementRequestDto request) throws BaseError {

        CompletableFuture<Product> productFuture =  CompletableFuture.supplyAsync(() -> productService.getById(productId));

        CompletableFuture<UserResponseDto> userResponseFuture = CompletableFuture.supplyAsync(() ->
                userService.getOneUser(request.userId()));

        CompletableFuture<ProductMovementResponseDto> productMovementFuture = productFuture
                .thenCombine(userResponseFuture, (product, userResponse) -> {

                    product.setQuantity(getNewQuantityProduct(request.movementType(), request.quantity(), product.getQuantity()));
                    product.setModifiedByUserId(userResponse.id());
                    productService.save(product);

                    ProductMovement productMovement = mapper.toEntity(request).toBuilder()
                            .product(product)
                            .userId(userResponse.id())
                            .build();

                    return mapper.toDto(productMovementRepository.save(productMovement));
                });

        try {
            return productMovementFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw errorUtil.handleAsyncException(e, ProductMovement.class);
        }
    }

    @Override
    @Transactional
    public ProductMovementResponseDto update(Long productId, Long id, ProductMovementRequestDto request) throws BaseError {

        ProductMovement productMovement = getByIdAndProductId(id, productId);
        UserResponseDto userResponse = userService.getOneUser(request.userId());

        Product product = productMovement.getProduct();
        Integer currentQuantity = revertPreviousMovementAndValidateStock(
                productMovement.getMovementType(),
                productMovement.getQuantity(),
                product.getQuantity()
        );


        product.setQuantity(currentQuantity);
        product.setQuantity(getNewQuantityProduct(request.movementType(), request.quantity(), product.getQuantity()));
        product.setModifiedByUserId(userResponse.id());
        productService.save(product);

        mapper.toUpdateEntity(request, productMovement);

        return mapper.toDto(productMovementRepository.save(productMovement));

    }

    private Integer getNewQuantityProduct(MovementTypes movementType, Integer movementQuantity, Integer currentStock) throws BaseError {
        return switch (movementType){
            case OUTPUT -> {
                if(movementQuantity > currentStock){
                    throw errorUtil.businessError(ERROR_PRODUCT_MOVEMENT_INSUFFICIENT_STOCK, ProductMovement.class);
                }
                yield (currentStock - movementQuantity);
            }
            case INPUT -> (currentStock + movementQuantity);
            default -> throw errorUtil.businessError(ERROR_PRODUCT_MOVEMENT_INVALID_MOMENT_TYPE, ProductMovement.class);
        };
    }

    private Integer revertPreviousMovementAndValidateStock(MovementTypes movementType, Integer movementQuantity, Integer currentStock) throws BaseError {
        Integer currentQuantity = currentStock;
        switch (movementType) {
            case INPUT -> currentQuantity -= movementQuantity;
            case OUTPUT -> currentQuantity += movementQuantity;
            default -> throw errorUtil.businessError(ERROR_PRODUCT_MOVEMENT_INVALID_MOMENT_TYPE, ProductMovement.class);
        }

        if (currentQuantity < 0) {
            throw errorUtil.businessError(ERROR_STOCK_NEGATIVE_AFTER_REVERT, ProductMovement.class);
        }

        return currentQuantity;
    }


    private ProductMovement getByIdAndProductId(Long id, Long productId) throws BaseError {
        return productMovementRepository.findByIdAndProductId(id, productId)
                .orElseThrow(() -> errorUtil.notFoundError(ERROR_PRODUCT_MOVEMENT_NOT_FOUND, ProductMovement.class));

    }

    @Override
    public void deactivate(Long productId, Long id, Long userId) throws BaseError {
        ProductMovement productMovement = getByIdAndProductId(id, productId);
        validateActiveProductMovement(productMovement);

        UserResponseDto userResponse = userService.getOneUser(userId);

        Product product = productMovement.getProduct();
        validateUserProductMovement(productMovement.getUserId(), userResponse.id());

        Integer currentQuantity = revertPreviousMovementAndValidateStock(
                productMovement.getMovementType(),
                productMovement.getQuantity(),
                product.getQuantity());

        product.setQuantity(currentQuantity);
        product.setModifiedByUserId(userResponse.id());
        productService.save(product);

        productMovement.setActive(Boolean.FALSE);
        productMovementRepository.save(productMovement);
    }


    private void validateUserProductMovement(Long productMovementUserId, Long userId) {
        if (!productMovementUserId.equals(userId)) {
            throw errorUtil.businessError(ERROR_PRODUCT_MOVEMENT_DELETE_INVALID_USER, ProductMovement.class);
        }
    }

    private void validateActiveProductMovement(ProductMovement productMovement) {
        if (productMovement.getActive().equals(Boolean.FALSE)) {
            throw errorUtil.businessError(ERROR_PRODUCT_MOVEMENT_DELETE_NOT_ACTIVE, ProductMovement.class);
        }
    }

}
