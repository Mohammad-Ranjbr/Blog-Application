package com.blog_application.service.user;

import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.user.*;
import com.blog_application.model.user.User;
import com.blog_application.util.responses.PaginatedResponse;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

public interface UserService {

    boolean isAdmin();
    String loggedInUserEmail();
    void deleteUserById(UUID userId) throws AccessDeniedException;
    User fetchUserById(UUID userId);
    UserGetDto getUserById(UUID userId);
    void savePost(UUID userId,Long postId) throws AccessDeniedException;
    void unSavePost(UUID userId,Long postId) throws AccessDeniedException;
    List<UserGetDto> getFollowers(UUID userId);
    List<UserGetDto> getFollowing(UUID userId);
    boolean isLoggedInUserMatching(UUID userId);
    void followUser(UUID userId,UUID followUserId) throws AccessDeniedException;
    void unfollowUser(UUID userId,UUID unfollowUserId) throws AccessDeniedException;
    List<PostGetDto> getSavedPostsByUser(UUID userId) throws AccessDeniedException;
    UserBasicInfoDto getUserBasicInfoById(UUID userId);
    UserGetDto createUser(UserCreateDto userCreateDto) throws IOException;
    void updateUserStatus(UUID userId, UserStatusUpdateDTO userStatusUpdateDTO) throws AccessDeniedException;
    UserGetDto updateUser(UserUpdateDto userUpdateDto,UUID userId) throws AccessDeniedException;
    PaginatedResponse<UserGetDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginatedResponse<UserBasicInfoDto> getAllBasicUserInfo(int pageNumber, int pageSize, String sortBy, String sortDir);

}
