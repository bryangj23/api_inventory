package com.nexos.api_inventory.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ProductResponseDto (
        Long id,
        String name,
        Integer quantity,
        Long registeredByUserId,
        Long modifiedByUserId,
        String description,
        Boolean active,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
){
}
