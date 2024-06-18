package com.blog_application.service;

import com.blog_application.dto.user.UserBasicInfoDto;
import com.blog_application.dto.user.UserCreateDto;
import com.blog_application.dto.user.UserGetDto;
import com.blog_application.dto.user.UserUpdateDto;

import java.util.List;

public interface UserService {

    List<UserGetDto> getAllUsers();
    void deleteUserById(Long userId);
    UserGetDto getUserById(Long userId);
    List<UserBasicInfoDto> getAllBasicUserInfo();
    UserBasicInfoDto getUserBasicInfoById(Long userId);
    UserGetDto createUser(UserCreateDto userCreateDto);
    UserGetDto updateUser(UserUpdateDto userUpdateDto,Long userId);

}
