package com.nexos.api_inventory.validator.enums;

import com.nexos.api_inventory.validator.ValidEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class EnumValidator implements ConstraintValidator<ValidEnum, Enum<?>> {
    private Class<? extends Enum> enumClass;
    private boolean required;

    @Override
    public void initialize(ValidEnum annotation) {
        this.enumClass = annotation.enumClass();
        this.required = annotation.required();
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {

        if(value == null) {
            return !required;
        }

        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(enumConstant -> enumConstant.name().equals(value.name()));

    }
}