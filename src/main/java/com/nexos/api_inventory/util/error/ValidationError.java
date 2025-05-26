package com.nexos.api_inventory.util.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class ValidationError extends BaseError {
    private final String code;
    private final String entityTypeName;
    private final List<ValidationDto> validation;

    public ValidationError(
            @JsonProperty("status") ErrorStatus status,
            @JsonProperty("message") String message,
            @JsonProperty("code") ErrorCode code,
            @JsonProperty("entityTypeName") String entityTypeName,
            @JsonProperty("validation") List<ValidationDto> validation) {
        super(status, message);
        this.code = code.toString();
        this.entityTypeName = entityTypeName;
        this.validation = validation;
    }

    @JsonCreator
    public ValidationError(
            @JsonProperty("status") ErrorStatus status,
            @JsonProperty("message") String message,
            @JsonProperty("code") String code,
            @JsonProperty("entityTypeName") String entityTypeName,
            @JsonProperty("validation") List<ValidationDto> validation) {
        super(status, message);
        this.code = code;
        this.entityTypeName = entityTypeName;
        this.validation = validation;
    }
}