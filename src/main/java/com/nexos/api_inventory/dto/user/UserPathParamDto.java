package com.nexos.api_inventory.dto.user;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPathParamDto {
    @NotBlank(message = "{common.error.required.field}")
    @Size(min = 1, message = "{common.error.str.exact.size}")
    @Digits(fraction = 0, integer = Integer.MAX_VALUE, message = "{common.error.integer}")
    @Positive(message = "{common.error.positive}")
    private String userId;

    public Long getUserId() {
        return Long.parseLong(this.userId);
    }
}
