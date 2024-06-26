package com.blog_application.service.impl;

import com.blog_application.config.mapper.UserMapper;
import com.blog_application.dto.user.UserBasicInfoDto;
import com.blog_application.dto.user.UserCreateDto;
import com.blog_application.dto.user.UserGetDto;
import com.blog_application.dto.user.UserUpdateDto;
import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.User;
import com.blog_application.repository.UserRepository;
import com.blog_application.service.UserService;
import com.blog_application.util.responses.PaginatedResponse;
import com.blog_application.util.utils.SortHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository,UserMapper userMapper){
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    @Override
    public PaginatedResponse<UserGetDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir) {
        logger.info("Fetching all users");

        Sort sort = SortHelper.getSortOrder(sortBy,sortDir);
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<User> userPage = userRepository.findAll(pageable);

        List<User> users = userPage.getContent();
        List<UserGetDto> userGetDtoList = users.stream()
                        .map(userMapper::toUserGetDto) //Method Reference
                                .toList();
        PaginatedResponse<UserGetDto> paginatedResponse = new PaginatedResponse<>(
                userGetDtoList,userPage.getSize(),userPage.getNumber(),userPage.getTotalPages(),userPage.getTotalElements(),userPage.isLast());
        logger.info("Total users found : {}",users.size());
        return paginatedResponse;
    }

    @Override
    public UserGetDto createUser(UserCreateDto userCreateDto) {
        logger.info("Creating user with email : {}",userCreateDto.getEmail());
        User user = userMapper.toEntity(userCreateDto);
        User savedUser = userRepository.save(user);
        logger.info("User created successfully with email : {}",savedUser.getEmail());
        return userMapper.toUserGetDto(savedUser);
    }

    @Override
    public void deleteUserById(UUID userId) {
        //Lambda Expression : Passing a function as an argument to another function
        //orElseThrow is a method that takes a Supplier as an argument. Supplier is a functional interface that accepts no parameters and returns a result. Here, the expected result is an exception.
        //Consumer is a functional interface in Java that takes an input and returns no result. Consumer is typically used for operations that take a parameter and return nothing (such as a print operation).
        //To use Consumer you must be sure that you want to perform an operation on an object and do not need to return a value.
        //For example, orElseThrow, which requires a Supplier, cannot use Consumer because its purpose is to create and return an exception.
        logger.info("Deleting user with ID : {}",userId);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            logger.warn("User with ID {} not found, Delete user operation not performed", userId);
            return new ResourceNotFoundException("User","ID",String.valueOf(userId),"Delete User operation not performed");
        });
        userRepository.delete(user);
        logger.info("User with ID {} deleted successfully",user.getId());
    }

    @Override
    public UserGetDto getUserById(UUID userId) {
        logger.info("Fetching user with ID : {}",userId);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            logger.warn("User with ID {} not found, Get user operation not performed", userId);
            return new ResourceNotFoundException("User","ID",String.valueOf(userId),"Get User operation not performed");
        });
        //Optional is a class that is used to prevent direct use of null and reduce problems related to NullPointerException.
        //This class is a container that may contain a value (which is present) or no value (which is empty).
        //Optional is a powerful tool for handling null values and makes code safer and more readable.
        //Correct use of Optional can help prevent NullPointerException and improve code readability and maintainability.
        //With Optional, you can easily check if a value exists and take the appropriate action.
        Optional<User> optionalUser = userRepository.findById(userId);
        Consumer<User> printUserDetails = foundUser -> System.out.println("User Found : " + foundUser);
        optionalUser.ifPresent(printUserDetails);
        logger.info("User found with ID : {}",user.getId());
        return userMapper.toUserGetDto(user);
    }

    @Override
    public PaginatedResponse<UserBasicInfoDto> getAllBasicUserInfo(int pageNumber, int pageSize, String sortBy, String sortDir) {
        logger.info("Fetching all user basic info");

        Sort sort = SortHelper.getSortOrder(sortBy,sortDir);
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<User> userPage = userRepository.findAll(pageable);

        List<User> users = userPage.getContent();
        List<UserBasicInfoDto> userGetDtoList = users.stream()
                .map(userMapper::toUserBasicInfoDto) //Method Reference
                .toList();

        PaginatedResponse<UserBasicInfoDto> paginatedResponse = new PaginatedResponse<>(
                userGetDtoList,userPage.getSize(),userPage.getNumber(),userPage.getTotalPages(),userPage.getTotalElements(),userPage.isLast());
        logger.info("Total user basic info found : {}",users.size());
        return paginatedResponse;
    }

    @Override
    public UserBasicInfoDto getUserBasicInfoById(UUID userId) {
        logger.info("Fetching user with ID : {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            logger.warn("User with ID {} not found, Get user operation not performed", userId);
            return new ResourceNotFoundException("User","ID",String.valueOf(userId),"Get User operation not performed");
        });
        logger.info("User found with ID : {}",userId);
        UserBasicInfoDto userBasicInfoDto = userMapper.toUserBasicInfoDto(user);
        logger.info("UserBasicInfoDto created: {}", userBasicInfoDto);
        return userBasicInfoDto;
    }

    @Override
    public UserGetDto updateUser(UserUpdateDto userUpdateDto, UUID userId) {
        logger.info("Updating user with ID : {}",userId);
        User updatedUser = userRepository.findById(userId).map(user -> {
            user.setName(userUpdateDto.getName());
            user.setEmail(userUpdateDto.getEmail());
            user.setAbout(userUpdateDto.getAbout());
            user.setGender(userUpdateDto.getGender());
            user.setUserName(userUpdateDto.getUserName());
            user.setPhoneNumber(userUpdateDto.getPhoneNumber());
            User savedUser = userRepository.save(user);
            logger.info("User with ID {} updated successfully",userId);
            return savedUser;
        }).orElseThrow(() -> {
            logger.warn("User with ID {} not found, Updated user operation not performed", userId);
            return new ResourceNotFoundException("User","ID",String.valueOf(userId),"Update User operation not performed");
        });
        return userMapper.toUserGetDto(updatedUser);
    }

}
