package com.nexos.api_inventory.service.impl;

import com.nexos.api_inventory.configuration.RestTemplateConfig;
import com.nexos.api_inventory.dto.user.UserResponseDto;
import com.nexos.api_inventory.service.IUserService;
import com.nexos.api_inventory.util.MappingUtils;
import com.nexos.api_inventory.util.error.BaseError;
import com.nexos.api_inventory.util.error.ErrorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private static final String ERROR_USER_UNKNOWN_EXCEPTION = "error.user_service.get_user.unknown_exception";

    @Value("${services.api-user.host}")
    private String apiUSerHost;

    private final ErrorUtil errorUtil;

    private final RestTemplate restTemplateWithoutTimeout = new RestTemplateConfig().restTemplateWithTimeout();

    @Override
    public UserResponseDto getOneUser(Long id) throws BaseError {
        try {
            log.info("Getting user by id {}", id);
            String url = apiUSerHost + "/business-support/user/v1/users/" + id;
            HttpHeaders defaultHeaders = buildHttpHeaders();
            ResponseEntity<String> response = restTemplateWithoutTimeout.exchange(url, HttpMethod.GET, new HttpEntity<>(defaultHeaders), String.class);
            return MappingUtils.responseToObject(UserResponseDto.class).apply(response);
        } catch (HttpClientErrorException.NotFound e) {
            log.error("User not found for id: {} error: {}", id, e.getMessage());
            throw errorUtil.handleResponseServiceNotFoundError(e, ERROR_USER_UNKNOWN_EXCEPTION, UserService.class);
        } catch (Exception e) {
            log.error("Error getting user, error: {}", e.getMessage());
            throw errorUtil.internalError(ERROR_USER_UNKNOWN_EXCEPTION, UserService.class);
        }
    }

    private HttpHeaders buildHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT, MediaType.ALL_VALUE);
        return headers;
    }

}
