package com.nexos.api_inventory.service.impl;

import com.nexos.api_inventory.dto.ProductRequestDto;
import com.nexos.api_inventory.dto.ProductResponseDto;
import com.nexos.api_inventory.dto.user.UserResponseDto;
import com.nexos.api_inventory.dto.user.role.RoleResponseDto;
import com.nexos.api_inventory.entity.Product;
import com.nexos.api_inventory.repository.ProductRepository;
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
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private IUserService userService;

    @Mock
    private JpaFilterHelper jpaFilterHelper;

    @Mock
    private ErrorUtil errorUtil;

    private Product product;
    private ProductRequestDto productRequestDto;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = Product.builder()
                .id(1L)
                .name("Product Name")
                .description("Product Description")
                .quantity(2)
                .registeredByUserId(1L)
                .modifiedByUserId(2L)
                .build();

        productRequestDto = ProductRequestDto.builder()
                .name("Product Name")
                .description("Product Description")
                .quantity(2)
                .userId(1L)
                .build();

        RoleResponseDto roleResponseDto = RoleResponseDto.builder()
                .id(1L)
                .code("administrator")
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .updatedAt(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        userResponseDto = UserResponseDto.builder()
                .id(1L)
                .userNumber("337477449")
                .email("test@email.com")
                .name("Name Test")
                .secondName("Second Name")
                .lastNames("Last Names")
                .mothersSurname("Mother Sur Name")
                .active(Boolean.TRUE)
                .role(roleResponseDto)
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .updatedAt(LocalDateTime.now(ZoneOffset.UTC))
                .build();

    }

    @Test
    void testGetAll() {

        Pageable pageable = mock(Pageable.class);
        String filters = "active==true";

        Page<Product> page = new PageImpl<>(Collections.singletonList(product));
        when(productRepository.findAll(any(), eq(pageable))).thenReturn(page);

        Page<ProductResponseDto> response = productService.getAll(pageable, filters);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(productRepository).findAll(any(), eq(pageable));
    }

    @Test
    void getOne() {

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        ProductResponseDto result = productService.getOne(1L);

        assertNotNull(result);
    }

    @Test
    void getById() {

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        Product result = productService.getById(1L);

        assertNotNull(result);
    }

    @Test
    void save() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.save(product);

        assertNotNull(result);
    }

    @Test
    void create() {
        when(productRepository.existsByName(anyString())).thenReturn(Boolean.FALSE);
        when(userService.getOneUser(anyLong())).thenReturn(userResponseDto);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDto result = productService.create(productRequestDto);

        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void update() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.existsByNameAndIdNot(anyString(), anyLong())).thenReturn(Boolean.FALSE);
        when(userService.getOneUser(anyLong())).thenReturn(userResponseDto);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDto result = productService.create(productRequestDto);

        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void delete() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(userService.getOneUser(anyLong())).thenReturn(userResponseDto);
        when(productRepository.existsProductsById(anyLong())).thenReturn(Boolean.FALSE);
        doNothing().when(productRepository).delete(any(Product.class));

        productService.delete(1L, 1L);

        verify(productRepository).delete(any(Product.class));
    }
}