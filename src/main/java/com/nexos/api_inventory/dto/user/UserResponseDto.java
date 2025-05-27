package com.nexos.api_inventory.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nexos.api_inventory.dto.user.role.RoleResponseDto;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponseDto(
       Long id,
       String userNumber,
       String email,
       String name,
       String secondName,
       String lastNames,
       String mothersSurname,
       Boolean active,
       RoleResponseDto role,
       @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
       LocalDateTime createdAt,
       @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
       LocalDateTime updatedAt
) {
}
