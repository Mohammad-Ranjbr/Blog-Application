package com.blog_application.service;

import com.blog_application.dto.user.UserDto;
import com.blog_application.dto.user.UserGetDto;
import com.blog_application.dto.user.UserUpdateDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();
    void deleteUserById(Long userId);
    UserDto getUserById(Long userId);
    UserDto createUser(UserDto userDto);
    UserGetDto updateUser(UserUpdateDto userDto, Long userId);

}
