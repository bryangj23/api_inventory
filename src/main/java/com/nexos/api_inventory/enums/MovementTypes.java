package com.nexos.api_inventory.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum MovementTypes {
    INPUT,
    OUTPUT;

    @JsonCreator
    public static MovementTypes fromValue(String value) {
        return Arrays.stream(MovementTypes.values())
                .filter(movementType -> movementType.name().equals(value))
                .findFirst()
                .orElse(null);
    }
}
