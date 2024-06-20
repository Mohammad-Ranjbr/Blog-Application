package com.blog_application.service;

import com.blog_application.dto.user.UserBasicInfoDto;
import com.blog_application.dto.user.UserCreateDto;
import com.blog_application.dto.user.UserGetDto;
import com.blog_application.dto.user.UserUpdateDto;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<UserGetDto> getAllUsers();
    void deleteUserById(UUID userId);
    UserGetDto getUserById(UUID userId);
    List<UserBasicInfoDto> getAllBasicUserInfo();
    UserBasicInfoDto getUserBasicInfoById(UUID userId);
    UserGetDto createUser(UserCreateDto userCreateDto);
    UserGetDto updateUser(UserUpdateDto userUpdateDto,UUID userId);

}
