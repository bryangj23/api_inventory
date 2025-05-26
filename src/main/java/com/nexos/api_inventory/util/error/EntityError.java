package com.nexos.api_inventory.util.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class EntityError extends BaseError {
    private final String code;
    private final String entityTypeName;

    public EntityError(
            @JsonProperty("status") ErrorStatus status,
            @JsonProperty("message") String message,
            @JsonProperty("code") ErrorCode code,
            @JsonProperty("entityTypeName") String entityTypeName) {
        super(status, message);
        this.code = code.toString();
        this.entityTypeName = entityTypeName;
    }

    @JsonCreator
    public EntityError(
            @JsonProperty("status") ErrorStatus status,
            @JsonProperty("message") String message,
            @JsonProperty("code") String code,
            @JsonProperty("entityTypeName") String entityTypeName) {
        super(status, message);
        this.code = code;
        this.entityTypeName = entityTypeName;
    }

}