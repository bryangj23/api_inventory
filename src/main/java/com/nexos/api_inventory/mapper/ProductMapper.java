package com.nexos.api_inventory.mapper;

import com.nexos.api_inventory.dto.ProductRequestDto;
import com.nexos.api_inventory.dto.ProductResponseDto;
import com.nexos.api_inventory.entity.Product;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "active", expression = "java(Boolean.TRUE)")
    Product toEntity(ProductRequestDto request);

    void toUpdateEntity(ProductRequestDto request, @MappingTarget Product product);

    ProductResponseDto toDto(Product entity);

}
