package com.nexos.api_inventory.util.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationDto implements Serializable {
    private String attribute;
    private String message;
}

