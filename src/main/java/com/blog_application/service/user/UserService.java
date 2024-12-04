package com.blog_application.service.user;

import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.user.UserBasicInfoDto;
import com.blog_application.dto.user.UserCreateDto;
import com.blog_application.dto.user.UserGetDto;
import com.blog_application.dto.user.UserUpdateDto;
import com.blog_application.model.user.User;
import com.blog_application.util.responses.PaginatedResponse;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

public interface UserService {

    void deleteUserById(UUID userId) throws AccessDeniedException;
    User fetchUserById(UUID userId);
    UserGetDto getUserById(UUID userId);
    void savePost(UUID userId,Long postId);
    void unSavePost(UUID userId,Long postId);
    List<UserGetDto> getFollowers(UUID userId);
    List<UserGetDto> getFollowing(UUID userId);
    void followUser(UUID userId,UUID followUserId);
    void unfollowUser(UUID userId,UUID unfollowUserId);
    List<PostGetDto> getSavedPostsByUser(UUID userId);
    UserBasicInfoDto getUserBasicInfoById(UUID userId);
    UserGetDto createUser(UserCreateDto userCreateDto);
    UserGetDto updateUser(UserUpdateDto userUpdateDto,UUID userId) throws AccessDeniedException;
    PaginatedResponse<UserGetDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginatedResponse<UserBasicInfoDto> getAllBasicUserInfo(int pageNumber, int pageSize, String sortBy, String sortDir);

}
