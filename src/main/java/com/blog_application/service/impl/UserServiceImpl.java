package com.blog_application.service.impl;

import com.blog_application.dto.UserDto;
import com.blog_application.model.User;
import com.blog_application.repository.UserRepository;
import com.blog_application.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,ModelMapper modelMapper){
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return null;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = this.userDtoToUser(userDto);
        User savedUser = userRepository.save(user);
        return this.userToUserDto(savedUser);
    }

    @Override
    public void deleteUserById(Long user_id) {

    }

    @Override
    public UserDto getUserById(Long user_id) {
        return null;
    }

    @Override
    public UserDto updateUser(UserDto user, Long user_id) {
        return null;
    }

    @Override
    public User userDtoToUser(UserDto userDto) {
        return modelMapper.map(userDto,User.class);
    }

    @Override
    public UserDto userToUserDto(User user) {
        return modelMapper.map(user,UserDto.class);
    }

}
