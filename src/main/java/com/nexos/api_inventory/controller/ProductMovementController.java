package com.nexos.api_inventory.controller;

import com.nexos.api_inventory.dto.ProductPathParamDto;
import com.nexos.api_inventory.dto.productmovement.ProductMovementPathParamDto;
import com.nexos.api_inventory.dto.productmovement.ProductMovementRequestDto;
import com.nexos.api_inventory.dto.productmovement.ProductMovementResponseDto;
import com.nexos.api_inventory.dto.user.UserPathParamDto;
import com.nexos.api_inventory.service.IProductMovementService;
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
public class ProductMovementController extends BaseController  {

    private final IProductMovementService productMovementService;
    private static final String COUNT_HEADER_NAME = "count";

    @GetMapping("/products/{productId}/product-movements")
    public ResponseEntity<List<ProductMovementResponseDto>> getAll(
            @PathVariable Long productId, Pageable pageable,
            @RequestParam(required = false, defaultValue = "") String filters)
            throws BaseError {
        Page<ProductMovementResponseDto> user = productMovementService.getAllByProduct(productId, pageable, filters);
        return ResponseEntity.ok()
                .header(COUNT_HEADER_NAME, String.valueOf(user.getTotalElements()))
                .body(user.stream().toList());
    }

    @GetMapping("/products/{productId}/product-movements/{productMovementId}")
    public ResponseEntity<ProductMovementResponseDto> getOne(
            @Valid ProductPathParamDto productPathParamDto,
            @Valid ProductMovementPathParamDto productMovementPathParamDto) throws BaseError {
        return ResponseEntity
                .ok(productMovementService.getOne(productPathParamDto.getProductId(),
                        productMovementPathParamDto.getProductMovementId()));
    }

    @PostMapping("/products/{productId}/product-movements")
    public ResponseEntity<ProductMovementResponseDto> create(
            @Valid ProductPathParamDto productPathParamDto,
            @RequestBody @Valid ProductMovementRequestDto productMovementRequestDto) throws BaseError {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productMovementService.create(productPathParamDto.getProductId(), productMovementRequestDto));
    }

    @PutMapping("/products/{productId}/product-movements/{productMovementId}")
    public ResponseEntity<ProductMovementResponseDto> update(
            @Valid ProductPathParamDto productPathParamDto,
            @Valid ProductMovementPathParamDto productMovementPathParamDto,
            @RequestBody @Valid ProductMovementRequestDto productMovementRequestDto) throws BaseError {
        return ResponseEntity
                .ok(productMovementService.update(productPathParamDto.getProductId(),
                        productMovementPathParamDto.getProductMovementId(), productMovementRequestDto));
    }

    @DeleteMapping("/products/{productId}/product-movements/{productMovementId}/users/{userId}")
    public ResponseEntity<Void> deactivate(@Valid ProductPathParamDto productPathParamDto,
                                           @Valid ProductMovementPathParamDto productMovementPathParamDto,
                                           @Valid UserPathParamDto userPathParamDto) throws BaseError {
        productMovementService.deactivate(productPathParamDto.getProductId(),
                productMovementPathParamDto.getProductMovementId(), userPathParamDto.getUserId());
        return ResponseEntity.noContent().build();
    }

}
