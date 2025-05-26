package com.nexos.api_inventory.service;

import com.nexos.api_inventory.dto.productmovement.ProductMovementRequestDto;
import com.nexos.api_inventory.dto.productmovement.ProductMovementResponseDto;
import com.nexos.api_inventory.util.error.BaseError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductMovementService {

    Page<ProductMovementResponseDto> getAllByProduct(Long productId, Pageable pageable, String filters) throws BaseError;

    ProductMovementResponseDto getOne(Long productId, Long id) throws BaseError;

    ProductMovementResponseDto create(Long productId, ProductMovementRequestDto request)  throws BaseError;

    ProductMovementResponseDto update(Long productId, Long id, ProductMovementRequestDto request) throws BaseError;

    void deactivate(Long productId, Long id, Long userId) throws BaseError;

}
