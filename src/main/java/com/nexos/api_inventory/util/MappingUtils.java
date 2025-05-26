package com.nexos.api_inventory.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexos.api_inventory.configuration.BaseConfig;
import com.nexos.api_inventory.util.error.EntityError;
import com.nexos.api_inventory.util.error.ErrorCode;
import com.nexos.api_inventory.util.error.ErrorStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

public class MappingUtils {

    private MappingUtils(){ }

    public static <T> Function<ResponseEntity<String>, T> responseToObject(Class<T> targetClass) {
        return response -> {
            try {
                ObjectMapper objectMapper = new BaseConfig().getObjectMapper();
                return objectMapper.readValue(response.getBody(), targetClass);
            } catch (JsonProcessingException e) {
                throw new EntityError(
                        ErrorStatus.BAD_REQUEST,
                        e.getMessage(),
                        ErrorCode.UNPROCESSABLE_CONTENT, targetClass.getSimpleName());
            }
        };
    }


}
