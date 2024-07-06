package com.blog_application.service.user;

import com.blog_application.dto.user.UserBasicInfoDto;
import com.blog_application.dto.user.UserCreateDto;
import com.blog_application.dto.user.UserGetDto;
import com.blog_application.dto.user.UserUpdateDto;
import com.blog_application.util.responses.PaginatedResponse;

import java.util.UUID;

public interface UserService {

    void deleteUserById(UUID userId);
    UserGetDto getUserById(UUID userId);
    UserBasicInfoDto getUserBasicInfoById(UUID userId);
    UserGetDto createUser(UserCreateDto userCreateDto);
    UserGetDto updateUser(UserUpdateDto userUpdateDto,UUID userId);
    PaginatedResponse<UserGetDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginatedResponse<UserBasicInfoDto> getAllBasicUserInfo(int pageNumber, int pageSize, String sortBy, String sortDir);

}
