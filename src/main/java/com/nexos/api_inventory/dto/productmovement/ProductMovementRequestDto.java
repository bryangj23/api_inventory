package com.nexos.api_inventory.dto.productmovement;

import com.nexos.api_inventory.enums.MovementTypes;
import com.nexos.api_inventory.validator.ValidEnum;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record ProductMovementRequestDto (
        @ValidEnum(enumClass = MovementTypes.class, message = "{error.product_movement.movement_type.invalid}", required = true)
        MovementTypes movementType,
        @NotNull(message = "{common.error.required.field}")
        @Digits(fraction = 0, integer = Integer.MAX_VALUE, message = "{common.error.integer}")
        @Positive(message = "{common.error.positive}")
        Integer quantity,
        @NotNull(message = "{common.error.required.field}")
        @Digits(fraction = 0, integer = Integer.MAX_VALUE, message = "{common.error.integer}")
        @Positive(message = "{common.error.positive}")
        Long userId,
        @Size(max = 255, message = "{error.product_movement.description.size}")
        @Pattern(regexp = "^[a-zA-Z ]+$", message = "{error.product_movement.description.invalid_pattern}")
        String description
){
}
