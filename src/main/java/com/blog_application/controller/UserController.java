package com.blog_application.controller;

import com.blog_application.dto.user.UserBasicInfoDto;
import com.blog_application.dto.user.UserCreateDto;
import com.blog_application.dto.user.UserGetDto;
import com.blog_application.dto.user.UserUpdateDto;
import com.blog_application.service.UserService;
import com.blog_application.util.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    //POST Mapping-Create User
    @PostMapping("/")
    public ResponseEntity<UserGetDto> createUser(@Valid @RequestBody UserCreateDto userCreateDto){
        logger.info("Received request to create user with email : {}", userCreateDto.getEmail());
        UserGetDto createdUser = this.userService.createUser(userCreateDto);
        logger.info("Returning response for user creation with email : {}", createdUser.getEmail());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    //GET Mapping-Get User By ID
    @GetMapping("/{id}")
    public ResponseEntity<UserGetDto> getUserById(@PathVariable("id") Long userId){
        logger.info("Received request to get user with ID : {}",userId);
        UserGetDto userGetDto = userService.getUserById(userId);
        logger.info("Returning response for get user with ID : {}",userGetDto.getId());
        return new ResponseEntity<>(userGetDto,HttpStatus.OK);
    }

    //PUT Mapping-Update User
    @PutMapping("/{id}")
    public ResponseEntity<UserGetDto> updateUser(@Valid @RequestBody UserUpdateDto userUpdateDto, @PathVariable("id") Long userId){
        logger.info("Received request to update user with ID : {}",userId);
        UserGetDto updatedUser = userService.updateUser(userUpdateDto,userId);
        logger.info("Returning response for updated user with ID : {}",userId);
        return new ResponseEntity<>(updatedUser,HttpStatus.OK);
    }

    //DELETE Mapping-Delete User
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable("id") Long userId){
        logger.info("Received request to delete user with ID : {}",userId);
        userService.deleteUserById(userId);
        logger.info("Returning response for delete user with ID : {}",userId);
        return new ResponseEntity<>(new ApiResponse("User Deleted Successfully",true),HttpStatus.OK);
    }

    //GET Mapping-Get All Users
    @GetMapping("/")
    public ResponseEntity<List<UserGetDto>> getAllUsers(){
        logger.info("Received request to get all users");
        List<UserGetDto> users = userService.getAllUsers();
        logger.info("Returning response with {} users",users.size());
        return new ResponseEntity<>(users,HttpStatus.OK);
    }

    //GET Mapping-Get User Basic Info By ID
    @GetMapping("/basic-info/{id}")
    public ResponseEntity<UserBasicInfoDto> getUserBasicInfo(@PathVariable("id") Long userId){
        logger.info("Received request to get user basic info with ID : {}",userId);
        UserBasicInfoDto userBasicInfoDto = userService.getUserBasicInfoById(userId);
        logger.info("Returning response for get user basic info with ID : {}",userId);
        return new ResponseEntity<>(userBasicInfoDto,HttpStatus.OK);
    }

    //GET Mapping-Get All User Basic Info
    @GetMapping("/basic-info/")
    public ResponseEntity<List<UserBasicInfoDto>> getAllUserBasicInfo(){
        logger.info("Received request to get all user basic info");
        List<UserBasicInfoDto> userBasicInfoDtos = userService.getAllBasicUserInfo();
        logger.info("Returning response with {} user basic info",userBasicInfoDtos.size());
        return new ResponseEntity<>(userBasicInfoDtos,HttpStatus.OK);
    }

    //OPTIONS Mapping for all users
    @RequestMapping(value = "/",method = RequestMethod.OPTIONS)
    public ResponseEntity<?> optionsForAllUsers(){
        logger.info("Received OPTIONS request for all users");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow","GET,POST,OPTIONS");
        logger.info("Returning response with allowed methods for all users");
        return new ResponseEntity<>(headers,HttpStatus.OK);
    }

    //OPTIONS Mapping for single user by ID
    @RequestMapping(value = "/{id}",method = RequestMethod.OPTIONS)
    public ResponseEntity<?> optionsForSingleUser(@PathVariable("id") Long userId){
        logger.info("Received OPTIONS request for user with ID : {}", userId);
        ResponseEntity<?> response = ResponseEntity.ok()
                .allow(HttpMethod.GET,HttpMethod.PUT,HttpMethod.DELETE,HttpMethod.OPTIONS)
                .build();
        logger.info("Returning response with allowed methods for user with ID : {}", userId);
        return response;
    }

}
