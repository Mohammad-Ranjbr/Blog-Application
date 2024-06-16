package com.blog_application.service;

import com.blog_application.dto.UserDto;
import com.blog_application.model.User;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();
    UserDto createUser(UserDto userDto);
    void deleteUserById(Long user_id);
    UserDto getUserById(Long user_id);
    UserDto updateUser(UserDto userDto,Long user_id);

}
