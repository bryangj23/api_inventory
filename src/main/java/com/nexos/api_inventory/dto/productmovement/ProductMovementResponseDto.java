package com.nexos.api_inventory.dto.productmovement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nexos.api_inventory.enums.MovementTypes;

import java.time.LocalDateTime;

public record ProductMovementResponseDto (
        Long id,
        Long productId,
        MovementTypes movementType,
        Integer quantity,
        Long userId,
        String description,
        Boolean active,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
) {
}
