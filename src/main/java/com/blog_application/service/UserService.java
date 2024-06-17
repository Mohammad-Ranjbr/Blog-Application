package com.blog_application.service;

import com.blog_application.dto.user.UserCreateDto;
import com.blog_application.dto.user.UserDto;
import com.blog_application.dto.user.UserGetDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();
    void deleteUserById(Long userId);
    UserDto getUserById(Long userId);
    UserGetDto createUser(UserCreateDto userCreateDto);
    UserDto updateUser(UserDto userDto,Long userId);

}
