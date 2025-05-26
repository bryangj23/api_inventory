package com.nexos.api_inventory.mapper;

import com.nexos.api_inventory.dto.productmovement.ProductMovementRequestDto;
import com.nexos.api_inventory.dto.productmovement.ProductMovementResponseDto;
import com.nexos.api_inventory.entity.ProductMovement;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProductMovementMapper {

    ProductMovementMapper INSTANCE = Mappers.getMapper(ProductMovementMapper.class);

    @Mapping(target = "active", expression = "java(Boolean.TRUE)")
    ProductMovement toEntity(ProductMovementRequestDto request);

    void toUpdateEntity(ProductMovementRequestDto request, @MappingTarget ProductMovement entity);

    @Mapping(target = "productId", source = "product.id")
    ProductMovementResponseDto toDto(ProductMovement entity);
}
