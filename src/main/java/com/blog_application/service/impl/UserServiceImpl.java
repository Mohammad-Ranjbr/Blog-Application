package com.blog_application.service.impl;

import com.blog_application.dto.UserDto;
import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.User;
import com.blog_application.repository.UserRepository;
import com.blog_application.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository,ModelMapper modelMapper){
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAllUsers() {
        logger.info("Fetching all users");
        List<User> users = userRepository.findAll();
        logger.info("Total users found : {}",users.size());
        //Method Reference
        return users.stream().map(this::userToUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        logger.info("Creating user : {}",userDto.getEmail());
        User user = this.userDtoToUser(userDto);
        User savedUser = userRepository.save(user);
        logger.info("User created successfully : {}",user.getEmail());
        return this.userToUserDto(savedUser);
    }

    @Override
    public void deleteUserById(Long user_id) {
        //Lambda Expression : Passing a function as an argument to another function
        //orElseThrow is a method that takes a Supplier as an argument. Supplier is a functional interface that accepts no parameters and returns a result. Here, the expected result is an exception.
        //Consumer is a functional interface in Java that takes an input and returns no result. Consumer is typically used for operations that take a parameter and return nothing (such as a print operation).
        //To use Consumer you must be sure that you want to perform an operation on an object and do not need to return a value.
        //For example, orElseThrow, which requires a Supplier, cannot use Consumer because its purpose is to create and return an exception.
        logger.info("Deleting user with ID : {}",user_id);
        User user = userRepository.findById(user_id).orElseThrow(() -> new ResourceNotFoundException("User","ID",String.valueOf(user_id)));
        userRepository.delete(user);
        logger.info("User with ID {} deleted successfully",user.getId());
    }

    @Override
    public UserDto getUserById(Long user_id) {
        logger.info("Fetching user with ID : {}",user_id);
        User user = userRepository.findById(user_id).orElseThrow(() -> new ResourceNotFoundException("User","ID",String.valueOf(user_id)));
        //Optional is a class that is used to prevent direct use of null and reduce problems related to NullPointerException.
        // This class is a container that may contain a value (which is present) or no value (which is empty).
        //Optional is a powerful tool for handling null values and makes code safer and more readable.
        //Correct use of Optional can help prevent NullPointerException and improve code readability and maintainability.
        //With Optional, you can easily check if a value exists and take the appropriate action.
        Optional<User> optionalUser = userRepository.findById(user_id);
        Consumer<User> printUserDetails = foundUser -> System.out.println("User Found : " + foundUser);
        optionalUser.ifPresent(printUserDetails);
        logger.info("User found with ID : {}",user.getId());
        return this.userToUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long user_id) {
        logger.info("Updating user with ID : {}",user_id);
        User updatedUser = userRepository.findById(user_id).map(user -> {
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            user.setAbout(userDto.getAbout());
            user.setPassword(userDto.getPassword());
            User savedUser = userRepository.save(user);
            logger.info("User with ID {} updated successfully",user_id);
            return savedUser;
        }).orElseThrow(() -> {
            logger.warn("User with ID {} not found, update not performed", user_id);
            return new ResourceNotFoundException("User","ID",String.valueOf(user_id));
        });
        return this.userToUserDto(userRepository.save(updatedUser));
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
