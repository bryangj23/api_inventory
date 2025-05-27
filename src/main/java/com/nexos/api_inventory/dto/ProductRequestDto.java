package com.nexos.api_inventory.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record ProductRequestDto(
        @NotBlank(message = "{common.error.str.exact.size}")
        @Size(max = 25, message = "{error.product.name.size}")
        @Pattern(regexp = "^[a-zA-Z ]+$", message = "{error.product.name.invalid_pattern}")
        String name,
        @NotNull(message = "{common.error.required.field}")
        @Digits(fraction = 0, integer = Integer.MAX_VALUE, message = "{common.error.integer}")
        @Positive(message = "{common.error.positive}")
        Integer quantity,
        @NotNull(message = "{common.error.required.field}")
        @Digits(fraction = 0, integer = Integer.MAX_VALUE, message = "{common.error.integer}")
        @Positive(message = "{common.error.positive}")
        Long userId,
        @Size(max = 255, message = "{error.product.description.size}")
        @Pattern(regexp = "^[a-zA-Z ]+$", message = "{error.product.description.invalid_pattern}")
        String description
) {
}
