package com.blog_application.service.user;

import com.blog_application.dto.image.ImageData;
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
    UUID loggedInUserId();
    String loggedInUserEmail();
    List<UserGetDto> suggestUsers();
    void deleteUserById(UUID userId) throws AccessDeniedException;
    User fetchUserById(UUID userId);
    User fetchUserByEmail(String userEmail);
    UserGetDto getUserById(UUID userId);
    UserGetDto getUserWithImage(UUID userId);
    List<UserBasicInfoDto> searchUsersByUsernameOrName(String keyword);
    void savePost(UUID userId,Long postId) throws AccessDeniedException;
    void unSavePost(UUID userId,Long postId) throws AccessDeniedException;
    List<UserGetDto> getFollowers(UUID userId);
    List<UserGetDto> getFollowing(UUID userId);
    boolean isLoggedInUserMatching(UUID userId);
    void setUserProfile(ImageData imageData, UUID userId) throws IOException;
    UserGetDto followUser(UUID userId,UUID followUserId) throws AccessDeniedException;
    UserGetDto unfollowUser(UUID userId,UUID unfollowUserId) throws AccessDeniedException;
    List<PostGetDto> getSavedPostsByUser(UUID userId) throws AccessDeniedException;
    UserBasicInfoDto getUserBasicInfoById(UUID userId);
    void createUser(UserCreateDto userCreateDto) throws IOException;
    void updateFollowedStatusForUsers(List<UserGetDto> userGetDtoList, String userEmail);
    void updateUserStatus(UUID userId, UserStatusUpdateDTO userStatusUpdateDTO) throws AccessDeniedException;
    void updateUser(UserUpdateDto userUpdateDto,UUID userId) throws AccessDeniedException;
    PaginatedResponse<UserGetDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginatedResponse<UserBasicInfoDto> getAllBasicUserInfo(int pageNumber, int pageSize, String sortBy, String sortDir);

}
