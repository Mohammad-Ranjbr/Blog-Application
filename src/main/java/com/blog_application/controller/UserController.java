package com.blog_application.controller;

import com.blog_application.dto.UserDto;
import com.blog_application.service.UserService;
import com.blog_application.util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto){
        logger.info("Received request to create user: {}", userDto.getEmail());
        UserDto createdUser = this.userService.createUser(userDto);
        logger.info("Returning response for user creation: {}", createdUser.getEmail());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    //GET Mapping-Get User By ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long user_id){
        logger.info("Received request to get user with ID : {}",user_id);
        UserDto userDto = userService.getUserById(user_id);
        logger.info("Returning response for user with ID : {}",userDto.getId());
        return new ResponseEntity<>(userDto,HttpStatus.OK);
    }

    //PUT Mapping-Update User
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto,@PathVariable("id") Long user_id){
        logger.info("Received request to update user with ID : {}",user_id);
        UserDto updatedUser = userService.updateUser(userDto,user_id);
        logger.info("Returning response for updated user with ID : {}",user_id);
        return new ResponseEntity<>(updatedUser,HttpStatus.OK);
    }

    //DELETE Mapping-Delete User
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable("id") Long user_id){
        logger.info("Received request to delete user with ID : {}",user_id);
        userService.deleteUserById(user_id);
        logger.info("Returning response for delete user with ID : {}",user_id);
        return new ResponseEntity<>(new ApiResponse("User Deleted Successfully",true),HttpStatus.OK);
    }

    //GET Mapping-Get All Users
    @GetMapping("/")
    public ResponseEntity<List<UserDto>> getAllUsers(){
        logger.info("Received request to get all users");
        List<UserDto> users = userService.getAllUsers();
        logger.info("Returning response with {} users",users.size());
        return new ResponseEntity<>(users,HttpStatus.OK);
    }

}
