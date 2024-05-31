package com.blog_application.service.impl;

import com.blog_application.dto.UserDto;
import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.User;
import com.blog_application.repository.UserRepository;
import com.blog_application.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        List<User> users = userRepository.findAll();
        return users.stream().map(this::userToUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = this.userDtoToUser(userDto);
        User savedUser = userRepository.save(user);
        return this.userToUserDto(savedUser);
    }

    @Override
    public void deleteUserById(Long user_id) {
        User user = userRepository.findById(user_id).orElseThrow(() -> new ResourceNotFoundException("User","ID",String.valueOf(user_id)));
        userRepository.delete(user);
    }

    @Override
    public UserDto getUserById(Long user_id) {
        User user = userRepository.findById(user_id).orElseThrow(() -> new ResourceNotFoundException("User","ID",String.valueOf(user_id)));
        return this.userToUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long user_id) {
        User user = this.userRepository.findById(user_id).orElseThrow(() -> new ResourceNotFoundException("User","ID",String.valueOf(user_id)));
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setAbout(userDto.getAbout());
        user.setPassword(userDto.getPassword());
        return this.userToUserDto(userRepository.save(user));
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
