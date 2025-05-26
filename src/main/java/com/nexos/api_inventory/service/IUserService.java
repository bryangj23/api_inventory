package com.nexos.api_inventory.service;

import com.nexos.api_inventory.dto.user.UserResponseDto;
import com.nexos.api_inventory.util.error.BaseError;

public interface IUserService {

    UserResponseDto getOneUser(Long id) throws BaseError;

}
