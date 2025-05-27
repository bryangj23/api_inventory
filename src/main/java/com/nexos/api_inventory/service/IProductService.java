package com.nexos.api_inventory.service;

import com.nexos.api_inventory.dto.ProductRequestDto;
import com.nexos.api_inventory.dto.ProductResponseDto;;
import com.nexos.api_inventory.entity.Product;
import com.nexos.api_inventory.util.error.BaseError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService {

    Page<ProductResponseDto> getAll(Pageable pageable, String filters) throws BaseError;

    ProductResponseDto getOne(Long id) throws BaseError;

    Product getById(Long id) throws BaseError;

    Product save(Product product) throws BaseError;

    ProductResponseDto create(ProductRequestDto request)  throws BaseError;

    ProductResponseDto update(Long id, ProductRequestDto request) throws BaseError;

    void delete(Long id, Long userId) throws BaseError;
}
