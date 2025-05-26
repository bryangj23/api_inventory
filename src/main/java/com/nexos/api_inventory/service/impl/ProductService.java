package com.nexos.api_inventory.service.impl;

import com.nexos.api_inventory.dto.ProductRequestDto;
import com.nexos.api_inventory.dto.ProductResponseDto;
import com.nexos.api_inventory.dto.user.UserResponseDto;
import com.nexos.api_inventory.entity.Product;
import com.nexos.api_inventory.mapper.ProductMapper;
import com.nexos.api_inventory.repository.ProductRepository;
import com.nexos.api_inventory.service.IProductService;
import com.nexos.api_inventory.service.IUserService;
import com.nexos.api_inventory.util.error.BaseError;
import com.nexos.api_inventory.util.error.ErrorUtil;
import com.nexos.api_inventory.util.jpafilter.JpaFilterHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private static final String ERROR_PRODUCT_NOT_FOUND = "error.product.not_found";
    private static final String ERROR_PRODUCT_NAME_ALREADY_EXISTS = "error.product.name.already_exists";
    private static final String ERROR_PRODUCT_DELETE_CONFLICT = "error.product.delete_conflict";
    private static final String ERROR_PRODUCT_DELETE_INVALID_USER = "error.product.delete.invalid_user";

    private final ProductRepository productRepository;
    private final IUserService userService;
    private final JpaFilterHelper jpaFilterHelper;
    private final ErrorUtil errorUtil;

    protected static final ProductMapper mapper = ProductMapper.INSTANCE;

    @Override
    public Page<ProductResponseDto> getAll(Pageable pageable, String filters) throws BaseError {
        return getAllProductsByFilters(pageable, filters)
                .map(mapper::toDto);
    }

    @Override
    public ProductResponseDto getOne(Long id) throws BaseError {
        return mapper.toDto(getById(id));
    }

    @Override
    public Product getById(Long id) throws BaseError {
        return productRepository.findById(id)
                .orElseThrow(() -> errorUtil.notFoundError(ERROR_PRODUCT_NOT_FOUND, Product.class));
    }

    @Override
    public void save(Product product) throws BaseError {
        productRepository.save(product);
    }

    @Override
    public ProductResponseDto create(ProductRequestDto request) throws BaseError {

        if (Boolean.TRUE.equals(productRepository.existsByName(request.name()))) {
            throw errorUtil.businessError(ERROR_PRODUCT_NAME_ALREADY_EXISTS, Product.class);
        }

        UserResponseDto userResponseDto = userService.getOneUser(request.userId());

        Product product = mapper.toEntity(request).toBuilder()
                .registeredByUserId(userResponseDto.id())
                .build();

        return mapper.toDto(productRepository.save(product));
    }

    @Override
    public ProductResponseDto update(Long id, ProductRequestDto request) throws BaseError {
        return productRepository.findById(id)
                .map(product -> {
                    if (Boolean.TRUE.equals(productRepository.existsByNameAndIdNot(request.name(), product.getId()))) {
                        throw errorUtil.businessError(ERROR_PRODUCT_NAME_ALREADY_EXISTS, Product.class);
                    }

                    UserResponseDto userResponseDto = userService.getOneUser(request.userId());

                    mapper.toUpdateEntity(request, product);
                    product.setModifiedByUserId(userResponseDto.id());

                    return product;
                })
                .map(productRepository::save)
                .map(mapper::toDto)
                .orElseThrow(() -> errorUtil.notFoundError(ERROR_PRODUCT_NOT_FOUND, Product.class));
    }

    @Override
    public void delete(Long id, Long userId) throws BaseError {

        Product product = getById(id);
        UserResponseDto userResponseDto = userService.getOneUser(userId);

        if (Boolean.TRUE.equals(productRepository.existsProductsById(product.getId()))) {
            throw errorUtil.businessError(ERROR_PRODUCT_DELETE_CONFLICT, Product.class);
        }

        if (!product.getRegisteredByUserId().equals(userResponseDto.id())) {
            throw errorUtil.businessError(ERROR_PRODUCT_DELETE_INVALID_USER, Product.class);
        }

        productRepository.delete(product);
    }

    private Page<Product> getAllProductsByFilters(Pageable pageable, String... filters) {
        return productRepository.findAll(jpaFilterHelper.doOnEvery(Specification::and, filters),
                pageable
        );
    }

}
