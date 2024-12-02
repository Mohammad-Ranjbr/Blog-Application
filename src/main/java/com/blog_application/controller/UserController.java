package com.blog_application.controller;

import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.user.UserBasicInfoDto;
import com.blog_application.dto.user.UserCreateDto;
import com.blog_application.dto.user.UserGetDto;
import com.blog_application.dto.user.UserUpdateDto;
import com.blog_application.service.user.UserService;
import com.blog_application.util.responses.ApiResponse;
import com.blog_application.util.constants.ApplicationConstants;
import com.blog_application.util.responses.PaginatedResponse;
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
import java.util.UUID;

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
    @PostMapping("/register")
    public ResponseEntity<UserGetDto> createUser(@Valid @RequestBody UserCreateDto userCreateDto){
        logger.info("Received request to create user with email : {}", userCreateDto.getEmail());
        UserGetDto createdUser = this.userService.createUser(userCreateDto);
        logger.info("Returning response for user creation with email : {}", createdUser.getEmail());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    //GET Mapping-Get User By ID
    @GetMapping("/get/{id}")
    public ResponseEntity<UserGetDto> getUserById(@PathVariable("id") UUID userId){
        logger.info("Received request to get user with ID : {}",userId);
        UserGetDto userGetDto = userService.getUserById(userId);
        logger.info("Returning response for get user with ID : {}",userGetDto.getId());
        return new ResponseEntity<>(userGetDto,HttpStatus.OK);
    }

    //PUT Mapping-Update User
    @PutMapping("/{id}")
    public ResponseEntity<UserGetDto> updateUser(@Valid @RequestBody UserUpdateDto userUpdateDto, @PathVariable("id") UUID userId){
        logger.info("Received request to update user with ID : {}",userId);
        UserGetDto updatedUser = userService.updateUser(userUpdateDto,userId);
        logger.info("Returning response for updated user with ID : {}",userId);
        return new ResponseEntity<>(updatedUser,HttpStatus.OK);
    }

    //DELETE Mapping-Delete User
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable("id") UUID userId){
        logger.info("Received request to delete user with ID : {}",userId);
        userService.deleteUserById(userId);
        logger.info("Returning response for delete user with ID : {}",userId);
        return new ResponseEntity<>(new ApiResponse("User Deleted Successfully",true),HttpStatus.OK);
    }

    //GET Mapping-Get All Users
    @GetMapping("/")
    public ResponseEntity<PaginatedResponse<UserGetDto>> getAllUsers(
            @RequestParam(value = ApplicationConstants.PAGE_NUMBER,defaultValue = ApplicationConstants.DEFAULT_PAGE_NUMBER,required = false) int pageNumber,
            @RequestParam(value = ApplicationConstants.PAGE_SIZE,defaultValue = ApplicationConstants.DEFAULT_PAGE_SIZE,required = false) int pageSize,
            @RequestParam(value = ApplicationConstants.SORT_BY,defaultValue = ApplicationConstants.DEFAULT_USER_SORT_BY,required = false) String sortBy,
            @RequestParam(value = ApplicationConstants.SORT_DIR,defaultValue = ApplicationConstants.DEFAULT_SORT_DIR,required = false) String sortDir){

        logger.info("Received request to get all users");
        PaginatedResponse<UserGetDto> users = userService.getAllUsers(pageNumber,pageSize,sortBy,sortDir);
        logger.info("Returning response all users");
        return new ResponseEntity<>(users,HttpStatus.OK);
    }

    //GET Mapping-Get User Basic Info By ID
    @GetMapping("/basic-info/{id}")
    public ResponseEntity<UserBasicInfoDto> getUserBasicInfo(@PathVariable("id") UUID userId){
        logger.info("Received request to get user basic info with ID : {}",userId);
        UserBasicInfoDto userBasicInfoDto = userService.getUserBasicInfoById(userId);
        logger.info("Returning response for get user basic info with ID : {}",userId);
        return new ResponseEntity<>(userBasicInfoDto,HttpStatus.OK);
    }

    //GET Mapping-Get All User Basic Info
    @GetMapping("/basic-info/")
    public ResponseEntity<PaginatedResponse<UserBasicInfoDto>> getAllUserBasicInfo(
            @RequestParam(value = ApplicationConstants.PAGE_NUMBER,defaultValue = ApplicationConstants.DEFAULT_PAGE_NUMBER,required = false) int pageNumber,
            @RequestParam(value = ApplicationConstants.PAGE_SIZE,defaultValue = ApplicationConstants.DEFAULT_PAGE_SIZE,required = false) int pageSize,
            @RequestParam(value = ApplicationConstants.SORT_BY,defaultValue = ApplicationConstants.DEFAULT_USER_SORT_BY,required = false) String sortBy,
            @RequestParam(value = ApplicationConstants.SORT_DIR,defaultValue = ApplicationConstants.DEFAULT_SORT_DIR,required = false) String sortDir){

        logger.info("Received request to get all user basic info");
        PaginatedResponse<UserBasicInfoDto> userBasicInfoDtos = userService.getAllBasicUserInfo(pageNumber,pageSize,sortBy,sortDir);
        logger.info("Returning response with all user basic info");
        return new ResponseEntity<>(userBasicInfoDtos,HttpStatus.OK);
    }

    //OPTIONS Mapping for all users
    @RequestMapping(value = "/",method = RequestMethod.OPTIONS)
    public ResponseEntity<?> optionsForAllUsers(){
        logger.info("Received OPTIONS request for all users");
        HttpHeaders headers = new HttpHeaders();
        headers.add(ApplicationConstants.HEADER_ALLOW,"GET,POST,OPTIONS");
        logger.info("Returning response with allowed methods for all users");
        return new ResponseEntity<>(headers,HttpStatus.OK);
    }

    //OPTIONS Mapping for single user by ID
    @RequestMapping(value = "/{id}",method = RequestMethod.OPTIONS)
    public ResponseEntity<?> optionsForSingleUser(@PathVariable("id") UUID userId){
        logger.info("Received OPTIONS request for user with ID : {}", userId);
        ResponseEntity<?> response = ResponseEntity.ok()
                .allow(HttpMethod.GET,HttpMethod.PUT,HttpMethod.DELETE,HttpMethod.OPTIONS)
                .build();
        logger.info("Returning response with allowed methods for user with ID : {}", userId);
        return response;
    }

    //POST Mapping - Save Post By User
    @PostMapping("/{user_id}/save_post/{post_id}")
    public ResponseEntity<ApiResponse> savePost(@PathVariable("user_id") UUID userId, @PathVariable("post_id") Long postId){
        logger.info("Received request to save post with ID: {} for user with ID: {}", postId, userId);
        userService.savePost(userId,postId);
        logger.info("Returning response with post ID: {} saved successfully for user with ID: {}", postId, userId);
        return new ResponseEntity<>(new ApiResponse("Post saved successfully",true),HttpStatus.OK);
    }

    //DELETE Mapping - UnSave Post By User
    @DeleteMapping("/{user_id}/unsave_post/{post_id}")
    public ResponseEntity<ApiResponse> unSavePost(@PathVariable("user_id") UUID userId, @PathVariable("post_id") Long postId){
        logger.info("Received request to unsave post with ID: {} for user with ID: {}", postId, userId);
        userService.unSavePost(userId,postId);
        logger.info("Returning response with post ID: {} unsaved successfully for user with ID: {}", postId, userId);
        return new ResponseEntity<>(new ApiResponse("post unsaved successfully",true),HttpStatus.OK);
    }

    //GET Mapping - Get User Saved Posts
    @GetMapping("/saved/{user_id}")
    public ResponseEntity<List<PostGetDto>> getSavedPostsByUser(@PathVariable("user_id") UUID userId){
        logger.info("Received request to get saved posts for user with ID: {}", userId);
        List<PostGetDto> savedPosts = userService.getSavedPostsByUser(userId);
        logger.info("Returning response {} saved posts for user with ID: {}", savedPosts.size(), userId);
        return new ResponseEntity<>(savedPosts,HttpStatus.OK);
    }

    //POST Mapping - Follow User
    @PostMapping("/{user_id}/follow/{follow_user_id}")
    public ResponseEntity<ApiResponse> followUser(@PathVariable("user_id") UUID userId, @PathVariable("follow_user_id") UUID followUserId){
        logger.info("Received request for user with ID: {} to follow user with ID: {}", userId, followUserId);
        userService.followUser(userId,followUserId);
        logger.info("User with ID: {} followed user with ID: {} successfully", userId, followUserId);
        return new ResponseEntity<>(new ApiResponse("Followed user successfully",true),HttpStatus.OK);
    }

    //DELETE Mapping - Unfollow User
    @DeleteMapping("/{user_id}/unfollow/{unfollow_user_id}")
    public ResponseEntity<ApiResponse> unfollowUser(@PathVariable("user_id") UUID userId,@PathVariable("unfollow_user_id") UUID unfollowUserId){
        logger.info("Received request for user with ID: {} to unfollow user with ID: {}", userId, unfollowUserId);
        userService.unfollowUser(userId,unfollowUserId);
        logger.info("User with ID: {} unfollowed user with ID: {} successfully", userId, unfollowUserId);
        return new ResponseEntity<>(new ApiResponse("Unfollowed user successfully",true),HttpStatus.OK);
    }

    //GET Mapping - Get User Followers
    @GetMapping("/{user_id}/followers")
    public ResponseEntity<List<UserGetDto>> getFollowers(@PathVariable("user_id") UUID userId){
        logger.info("Received request to get followers for user with ID: {}", userId);
        List<UserGetDto> followers = userService.getFollowers(userId);
        logger.info("Returning {} followers for user with ID: {}", followers.size(), userId);
        return new ResponseEntity<>(followers,HttpStatus.OK);
    }

    //GET Mapping - Get User Following
    @GetMapping("/{user_id}/following")
    public ResponseEntity<List<UserGetDto>> getFollowing(@PathVariable("user_id") UUID userId){
        logger.info("Received request to get following users for user with ID: {}", userId);
        List<UserGetDto> following = userService.getFollowing(userId);
        logger.info("Returning {} following users for user with ID: {}", following.size(), userId);
        return new ResponseEntity<>(following,HttpStatus.OK);
    }

}
