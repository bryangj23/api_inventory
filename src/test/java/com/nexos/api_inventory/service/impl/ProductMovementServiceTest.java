package com.nexos.api_inventory.service.impl;

import com.nexos.api_inventory.dto.productmovement.ProductMovementRequestDto;
import com.nexos.api_inventory.dto.productmovement.ProductMovementResponseDto;
import com.nexos.api_inventory.dto.user.UserResponseDto;
import com.nexos.api_inventory.entity.Product;
import com.nexos.api_inventory.entity.ProductMovement;
import com.nexos.api_inventory.enums.MovementTypes;
import com.nexos.api_inventory.repository.ProductMovementRepository;
import com.nexos.api_inventory.service.IProductService;
import com.nexos.api_inventory.service.IUserService;
import com.nexos.api_inventory.util.error.ErrorUtil;
import com.nexos.api_inventory.util.jpafilter.JpaFilterHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductMovementServiceTest {

    @InjectMocks
    private ProductMovementService productMovementService;

    @Mock
    private ProductMovementRepository productMovementRepository;

    @Mock
    private IProductService productService;

    @Mock
    private IUserService userService;

    @Mock
    private JpaFilterHelper jpaFilterHelper;

    @Mock
    private ErrorUtil errorUtil;

    private Product product;
    private ProductMovement productMovement;
    private ProductMovementRequestDto requestDto;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = Product.builder()
                .id(1L)
                .name("Product")
                .quantity(10)
                .build();

        productMovement = ProductMovement.builder()
                .id(1L)
                .movementType(MovementTypes.OUTPUT)
                .quantity(2)
                .product(product)
                .userId(1L)
                .active(true)
                .build();

        requestDto = ProductMovementRequestDto.builder()
                .movementType(MovementTypes.OUTPUT)
                .quantity(2)
                .userId(1L)
                .build();

        userResponseDto = UserResponseDto.builder()
                .id(1L)
                .name("User")
                .active(true)
                .build();
    }

    @Test
    void testGetAllByProduct() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductMovement> page = new PageImpl<>(List.of(productMovement));

        when(productMovementRepository.findAll(any(), eq(pageable))).thenReturn(page);

        Page<ProductMovementResponseDto> result = productMovementService.getAllByProduct(1L, pageable, "");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetOne() {
        when(productMovementRepository.findByIdAndProductId(1L, 1L)).thenReturn(Optional.of(productMovement));

        ProductMovementResponseDto result = productMovementService.getOne(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.userId());
    }

    @Test
    void testCreate() throws Exception {
        when(productService.getById(anyLong())).thenReturn(product);
        when(userService.getOneUser(anyLong())).thenReturn(userResponseDto);
        when(productMovementRepository.save(any())).thenReturn(productMovement);
        when(productService.save(any())).thenReturn(product);

        ProductMovementResponseDto result = productMovementService.create(1L, requestDto);

        assertNotNull(result);
        assertEquals(1L, result.userId());
    }

    @Test
    void testUpdate() {
        when(productMovementRepository.findByIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.of(productMovement));
        when(userService.getOneUser(anyLong())).thenReturn(userResponseDto);
        when(productService.save(any())).thenReturn(product);
        when(productMovementRepository.save(any())).thenReturn(productMovement);

        ProductMovementResponseDto result = productMovementService.update(1L, 1L, requestDto);

        assertNotNull(result);
        assertEquals(1L, result.userId());
    }

    @Test
    void testDeactivate() {
        when(productMovementRepository.findByIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.of(productMovement));
        when(userService.getOneUser(anyLong())).thenReturn(userResponseDto);
        when(productService.save(any())).thenReturn(product);
        when(productMovementRepository.save(any())).thenReturn(productMovement);

        productMovementService.deactivate(1L, 1L, 1L);

        verify(productMovementRepository).save(any());
    }
}