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
import java.util.Optional;
import java.util.function.Consumer;
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
        //Lambda Expression : Passing a function as an argument to another function
        //orElseThrow is a method that takes a Supplier as an argument. Supplier is a functional interface that accepts no parameters and returns a result. Here, the expected result is an exception.
        //Consumer is a functional interface in Java that takes an input and returns no result. Consumer is typically used for operations that take a parameter and return nothing (such as a print operation).
        //To use Consumer you must be sure that you want to perform an operation on an object and do not need to return a value.
        //For example, orElseThrow, which requires a Supplier, cannot use Consumer because its purpose is to create and return an exception.
        User user = userRepository.findById(user_id).orElseThrow(() -> new ResourceNotFoundException("User","ID",String.valueOf(user_id)));
        userRepository.delete(user);
    }

    @Override
    public UserDto getUserById(Long user_id) {
        User user = userRepository.findById(user_id).orElseThrow(() -> new ResourceNotFoundException("User","ID",String.valueOf(user_id)));
        //Optional is a class that is used to prevent direct use of null and reduce problems related to NullPointerException.
        // This class is a container that may contain a value (which is present) or no value (which is empty).
        //Optional is a powerful tool for handling null values and makes code safer and more readable.
        //Correct use of Optional can help prevent NullPointerException and improve code readability and maintainability.
        //With Optional, you can easily check if a value exists and take the appropriate action.
        Optional<User> optionalUser = userRepository.findById(user_id);
        Consumer<User> printUserDetails = foundUser -> System.out.println("User Found : " + foundUser);
        optionalUser.ifPresent(printUserDetails);
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
