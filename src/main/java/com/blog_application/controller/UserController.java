package com.blog_application.controller;

import com.blog_application.dto.image.ImageData;
import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.user.*;
import com.blog_application.service.user.UserService;
import com.blog_application.util.responses.ApiResponse;
import com.blog_application.util.constants.ApplicationConstants;
import com.blog_application.util.responses.PaginatedResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
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
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody UserCreateDto userCreateDto) throws IOException {
        logger.info("Received request to create user with email : {}", userCreateDto.getEmail());
        userService.createUser(userCreateDto);
        logger.info("Returning response for user creation with email : {}", userCreateDto.getEmail());
        return new ResponseEntity<>(new ApiResponse("User Created Successfully", true), HttpStatus.CREATED);
    }

    //GET Mapping-Get User By ID
    @GetMapping("/{id}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<UserGetDto> getUserById(@PathVariable("id") UUID userId){
        logger.info("Received request to get user with ID : {}",userId);
        UserGetDto userGetDto = userService.getUserWithImage(userId);
        logger.info("Returning response for get user with ID : {}",userGetDto.getId());
        return new ResponseEntity<>(userGetDto,HttpStatus.OK);
    }

    //PUT Mapping-Update User
    @PutMapping("/{id}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<ApiResponse> updateUser(@Valid @RequestBody UserUpdateDto userUpdateDto, @PathVariable("id") UUID userId) throws AccessDeniedException {
        logger.info("Received request to update user with ID : {}",userId);
        userService.updateUser(userUpdateDto,userId);
        logger.info("Returning response for updated user with ID : {}",userId);
        return new ResponseEntity<>(new ApiResponse("User updated successfully.", true),HttpStatus.OK);
    }

    //DELETE Mapping-Delete User
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable("id") UUID userId) throws AccessDeniedException {
        logger.info("Received request to delete user with ID : {}",userId);
        userService.deleteUserById(userId);
        logger.info("Returning response for delete user with ID : {}",userId);
        return new ResponseEntity<>(new ApiResponse("User Deleted Successfully",true),HttpStatus.OK);
    }

    //GET Mapping-Get All Users
    @GetMapping("/")
    @SecurityRequirement(name = "Jwt Token Authentication")
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
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<UserBasicInfoDto> getUserBasicInfo(@PathVariable("id") UUID userId){
        logger.info("Received request to get user basic info with ID : {}",userId);
        UserBasicInfoDto userBasicInfoDto = userService.getUserBasicInfoById(userId);
        logger.info("Returning response for get user basic info with ID : {}",userId);
        return new ResponseEntity<>(userBasicInfoDto,HttpStatus.OK);
    }

    //GET Mapping-Get All User Basic Info
    @GetMapping("/basic-info/")
    @SecurityRequirement(name = "Jwt Token Authentication")
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

    //POST Mapping - Save Post By User
    @PostMapping("/{user_id}/save_post/{post_id}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<ApiResponse> savePost(@PathVariable("user_id") UUID userId, @PathVariable("post_id") Long postId) throws AccessDeniedException {
        logger.info("Received request to save post with ID: {} for user with ID: {}", postId, userId);
        userService.savePost(userId,postId);
        logger.info("Returning response with post ID: {} saved successfully for user with ID: {}", postId, userId);
        return new ResponseEntity<>(new ApiResponse("Post saved successfully.", true),HttpStatus.OK);
    }

    //DELETE Mapping - UnSave Post By User
    @DeleteMapping("/{user_id}/unsave_post/{post_id}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<ApiResponse> unSavePost(@PathVariable("user_id") UUID userId, @PathVariable("post_id") Long postId) throws AccessDeniedException {
        logger.info("Received request to unsave post with ID: {} for user with ID: {}", postId, userId);
        userService.unSavePost(userId,postId);
        logger.info("Returning response with post ID: {} unsaved successfully for user with ID: {}", postId, userId);
        return new ResponseEntity<>(new ApiResponse("Post unsaved successfully.", true),HttpStatus.OK);
    }

    //GET Mapping - Get User Saved Posts
    @GetMapping("/saved/{user_id}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<List<PostGetDto>> getSavedPostsByUser(@PathVariable("user_id") UUID userId) throws AccessDeniedException {
        logger.info("Received request to get saved posts for user with ID: {}", userId);
        List<PostGetDto> savedPosts = userService.getSavedPostsByUser(userId);
        logger.info("Returning response {} saved posts for user with ID: {}", savedPosts.size(), userId);
        return new ResponseEntity<>(savedPosts,HttpStatus.OK);
    }

    //POST Mapping - Follow User
    @PostMapping("/{user_id}/follow/{follow_user_id}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<UserGetDto> followUser(@PathVariable("user_id") UUID userId, @PathVariable("follow_user_id") UUID followUserId) throws AccessDeniedException {
        logger.info("Received request for user with ID: {} to follow user with ID: {}", userId, followUserId);
        UserGetDto userGetDto = userService.followUser(userId,followUserId);
        logger.info("User with ID: {} followed user with ID: {} successfully", userId, followUserId);
        return new ResponseEntity<>(userGetDto, HttpStatus.OK);
    }

    //DELETE Mapping - Unfollow User
    @DeleteMapping("/{user_id}/unfollow/{unfollow_user_id}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<UserGetDto> unfollowUser(@PathVariable("user_id") UUID userId,@PathVariable("unfollow_user_id") UUID unfollowUserId) throws AccessDeniedException {
        logger.info("Received request for user with ID: {} to unfollow user with ID: {}", userId, unfollowUserId);
        UserGetDto userGetDto = userService.unfollowUser(userId,unfollowUserId);
        logger.info("User with ID: {} unfollowed user with ID: {} successfully", userId, unfollowUserId);
        return new ResponseEntity<>(userGetDto, HttpStatus.OK);
    }

    //GET Mapping - Get User Followers
    @GetMapping("/{user_id}/followers")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<List<UserGetDto>> getFollowers(@PathVariable("user_id") UUID userId){
        logger.info("Received request to get followers for user with ID: {}", userId);
        List<UserGetDto> followers = userService.getFollowers(userId);
        logger.info("Returning {} followers for user with ID: {}", followers.size(), userId);
        return new ResponseEntity<>(followers,HttpStatus.OK);
    }

    //GET Mapping - Get User Following
    @GetMapping("/{user_id}/following")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<List<UserGetDto>> getFollowing(@PathVariable("user_id") UUID userId){
        logger.info("Received request to get following users for user with ID: {}", userId);
        List<UserGetDto> following = userService.getFollowing(userId);
        logger.info("Returning {} following users for user with ID: {}", following.size(), userId);
        return new ResponseEntity<>(following,HttpStatus.OK);
    }

    @PatchMapping("/{id}/status")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<ApiResponse> updateUserStatus(@PathVariable("id") UUID userId, @RequestBody UserStatusUpdateDTO userStatusUpdateDTO) throws AccessDeniedException {
        logger.info("Received request to update user status. User ID: {}, New Status: {}", userId, userStatusUpdateDTO.isActive());
        userService.updateUserStatus(userId, userStatusUpdateDTO);
        logger.info("User status updated successfully. User ID: {}, New Status: {}", userId, userStatusUpdateDTO.isActive());
        return new ResponseEntity<>(new ApiResponse("User status updated successfully", true), HttpStatus.OK);
    }

    @GetMapping("/search/{keyword}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<List<UserBasicInfoDto>> searchUserWithUsernameOrName(@PathVariable("keyword") String keyword){
        logger.info("Received request to search users by username or name : {}", keyword);
        List<UserBasicInfoDto> userBasicInfoDtoList = userService.searchUsersByUsernameOrName(keyword);
        logger.info("Returning response for search users by username or name : {}", keyword);
        return new ResponseEntity<>(userBasicInfoDtoList, HttpStatus.OK);
    }

    @GetMapping("/suggestions")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<List<UserGetDto>> suggestUsers() {
        logger.info("Received request to fetch suggested users for current user.");
        List<UserGetDto> suggestedUsers = userService.suggestUsers();
        logger.info("Returning response with {} suggested users.", suggestedUsers.size());
        return new ResponseEntity<>(suggestedUsers, HttpStatus.OK);
    }

    @PatchMapping("/{id}/profile-image")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<ApiResponse> setUserProfileImage(@PathVariable("id") UUID userId, @RequestBody ImageData imageData) throws IOException {
        logger.info("Received request to update profile image. User ID: {}", userId);
        userService.setUserProfile(imageData, userId);
        logger.info("Profile image updated successfully for User ID: {}", userId);
        return new ResponseEntity<>(new ApiResponse("Profile image updated successfully", true), HttpStatus.OK);
    }

}
