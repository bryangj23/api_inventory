package com.nexos.api_inventory.util.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexos.api_inventory.util.message.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class ErrorUtil {

    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    public EntityError businessError(String errorKey, Class<?> entityClass) {
        return new EntityError(
                ErrorStatus.BAD_REQUEST,
                messageService.getMessage(errorKey),
                ErrorCode.NO_VALID_BUSINESS_RULE,
                entityClass.getSimpleName());
    }

    public EntityError notFoundError(String errorKey, Class<?> entityClass) {
        return new EntityError(
                ErrorStatus.NOT_FOUND,
                messageService.getMessage(errorKey),
                ErrorCode.NOT_FOUND,
                entityClass.getSimpleName()
        );
    }

    public EntityError internalError(String errorKey, Class<?> entityClass) {
        return new EntityError(
                ErrorStatus.INTERNAL_SERVER_ERROR,
                messageService.getMessage(errorKey),
                ErrorCode.INTERNAL_SERVER_ERROR,
                entityClass.getSimpleName());
    }

    public EntityError handleAsyncException(Throwable ex, Class<?> entityClass) {
        if (ex.getCause() instanceof EntityError error) {
            return error;
        }

        return new EntityError(
                ErrorStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                ErrorCode.INTERNAL_SERVER_ERROR,
                entityClass.getSimpleName());
    }

    public EntityError handleResponseServiceNotFoundError(HttpClientErrorException.NotFound e, String errorKey, Class<?> entityClass) {
        try {
            return objectMapper.readValue(e.getResponseBodyAsString(), EntityError.class);
        } catch (IOException ex) {
            return internalError(errorKey, entityClass);
        }
    }

}
