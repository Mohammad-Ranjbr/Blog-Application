package com.blog_application.service;

import com.blog_application.dto.user.UserCreateDto;
import com.blog_application.dto.user.UserDto;
import com.blog_application.dto.user.UserGetDto;
import com.blog_application.dto.user.UserUpdateDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();
    void deleteUserById(Long userId);
    UserDto getUserById(Long userId);
    UserGetDto createUser(UserCreateDto userCreateDto);
    UserGetDto updateUser(UserUpdateDto userUpdateDto,Long userId);

}
