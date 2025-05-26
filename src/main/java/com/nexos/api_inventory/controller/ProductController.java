package com.nexos.api_inventory.controller;

import com.nexos.api_inventory.dto.ProductPathParamDto;
import com.nexos.api_inventory.dto.ProductRequestDto;
import com.nexos.api_inventory.dto.ProductResponseDto;
import com.nexos.api_inventory.dto.user.UserPathParamDto;
import com.nexos.api_inventory.service.IProductService;
import com.nexos.api_inventory.util.error.BaseError;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ProductController extends BaseController {

    private final IProductService productService;
    private static final String COUNT_HEADER_NAME = "count";

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDto>> getAll(Pageable pageable,
                                                           @RequestParam(required = false, defaultValue = "") String filters)
            throws BaseError {
        Page<ProductResponseDto> user = productService.getAll(pageable, filters);
        return ResponseEntity.ok()
                .header(COUNT_HEADER_NAME, String.valueOf(user.getTotalElements()))
                .body(user.stream().toList());
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponseDto> create(@RequestBody @Valid ProductRequestDto productRequestDto) throws BaseError {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.create(productRequestDto));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductResponseDto> getOne(@Valid ProductPathParamDto productPathParamDto) throws BaseError {
        return ResponseEntity
                .ok(productService.getOne(productPathParamDto.getProductId()));
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductResponseDto> update(
            @Valid ProductPathParamDto productPathParamDto,
            @RequestBody @Valid ProductRequestDto productRequestDto) throws BaseError {
        return ResponseEntity
                .ok(productService.update(productPathParamDto.getProductId(), productRequestDto));
    }

    @DeleteMapping("/products/{productId}/users/{userId}")
    public ResponseEntity<Void> delete(@Valid ProductPathParamDto productPathParamDto,
                                           @Valid UserPathParamDto userPathParamDto) throws BaseError {
        productService.delete(productPathParamDto.getProductId(), userPathParamDto.getUserId());
        return ResponseEntity.noContent().build();
    }

}
