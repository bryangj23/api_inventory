package com.nexos.api_inventory.util.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ServiceInternalError extends BaseError {
    private final String code;

    public ServiceInternalError(
            @JsonProperty("status") ErrorStatus status,
            @JsonProperty("message") String message,
            @JsonProperty("code") ErrorCode code) {
        super(status, message);
        this.code = code.toString();
    }

    @JsonCreator
    public ServiceInternalError(
            @JsonProperty("status") ErrorStatus status,
            @JsonProperty("message") String message,
            @JsonProperty("code") String code) {
        super(status, message);
        this.code = code;
    }
}

