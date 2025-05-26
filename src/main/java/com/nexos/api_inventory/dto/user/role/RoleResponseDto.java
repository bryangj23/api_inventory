package com.nexos.api_inventory.dto.user.role;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record RoleResponseDto(
        Long id,
        String code,
        String name,
        String description,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
) {
}
