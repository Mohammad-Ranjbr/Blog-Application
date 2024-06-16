package com.blog_application.service;

import com.blog_application.dto.UserDto;
import com.blog_application.model.User;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();
    void deleteUserById(Long userId);
    UserDto getUserById(Long userId);
    UserDto createUser(UserDto userDto);
    UserDto updateUser(UserDto userDto,Long userId);

}
