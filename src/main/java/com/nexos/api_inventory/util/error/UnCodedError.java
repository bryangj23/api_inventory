package com.nexos.api_inventory.util.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UnCodedError extends BaseError {

    public UnCodedError(
            @JsonProperty("status") ErrorStatus status,
            @JsonProperty("message")String message
    ) {
        super(status, message);
    }
}

