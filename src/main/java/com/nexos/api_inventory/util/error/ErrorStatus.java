package com.nexos.api_inventory.util.error;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ErrorStatus {
    NONE(0),
    OK(200),
    NO_CONTENT(204),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    GONE(410),
    UNSUPPORTED_MEDIA_TYPE(415),
    EXPECTATION_FAILED(417),
    UNPROCESSABLE_CONTENT(422),
    MANY_REQUEST(429),
    INTERNAL_SERVER_ERROR(500),
    BAD_GATEWAY(502),
    SERVICE_UNAVAILABLE(503),
    GATEWAY_TIMEOUT(504);

    private final int code;

    ErrorStatus(int code) {
        this.code = code;
    }

    @JsonValue
    public int getValue() {
        return code;
    }
}