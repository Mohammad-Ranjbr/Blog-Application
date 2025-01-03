package com.blog_application.service.impl.user;

import com.blog_application.config.mapper.user.UserMapper;
import com.blog_application.dto.image.ImageData;
import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.user.*;
import com.blog_application.exception.ResourceAlreadyExistsException;
import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.post.Post;
import com.blog_application.model.role.Role;
import com.blog_application.model.user.User;
import com.blog_application.repository.post.PostRepository;
import com.blog_application.repository.user.UserRepository;
import com.blog_application.service.image.ImageService;
import com.blog_application.service.post.PostService;
import com.blog_application.service.role.RoleService;
import com.blog_application.service.user.UserService;
import com.blog_application.util.responses.PaginatedResponse;
import com.blog_application.util.utils.SortHelper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Value("${user.default-image}")
    private String userDefaultImage;
    @Value("${minio.user-bucket-name}")
    private String userImagesBucket;
    private final UserMapper userMapper;
    private final PostService postService;
    private final RoleService roleService;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, RoleService roleService
            ,@Lazy PostService postService,PostRepository postRepository,PasswordEncoder passwordEncoder,
                           ImageService imageService){
        this.userMapper = userMapper;
        this.postService = postService;
        this.roleService = roleService;
        this.imageService = imageService;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PaginatedResponse<UserGetDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir) {
        logger.info("Fetching all users");

        Sort sort = SortHelper.getSortOrder(sortBy,sortDir);
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        try{
            Page<User> userPage = userRepository.findAll(pageable);

            List<User> users = userPage.getContent();
            List<UserGetDto> userGetDtoList = users.stream()
                    .map(userMapper::toUserGetDto) //Method Reference
                    .toList();

            this.updateFollowedStatusForUsers(userGetDtoList, this.loggedInUserEmail());

            PaginatedResponse<UserGetDto> paginatedResponse = new PaginatedResponse<>(
                    userGetDtoList,userPage.getSize(),userPage.getNumber(),userPage.getTotalPages(),userPage.getTotalElements(),userPage.isLast());
            logger.info("Total users found : {}",users.size());
            return paginatedResponse;
        } catch (Exception exception){
            logger.error("Unexpected error in getAllUsers: {}", exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    @Transactional
    public void createUser(UserCreateDto userCreateDto){
        logger.info("Creating user with username : {}",userCreateDto.getUserName());

        if(userRepository.existsByUserName(userCreateDto.getUserName())){
            logger.warn("User with Username {} already exists, Register user operation not performed", userCreateDto.getUserName());
            throw  new ResourceAlreadyExistsException("User","Username",String.valueOf(userCreateDto.getUserName()),"Register User operation not performed");
        }

        if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            logger.warn("User with Email {} already exists, Register user operation not performed", userCreateDto.getEmail());
            throw  new ResourceAlreadyExistsException("User","Email",String.valueOf(userCreateDto.getEmail()),"Register User operation not performed");
        }

        try {
            Role userRole = roleService.getRoleByName("ROLE_USER");
            String hashPassword = passwordEncoder.encode(userCreateDto.getPassword());
            User user = userMapper.toEntity(userCreateDto);
            user.setPassword(hashPassword);
            user.setRoles(Set.of(userRole));
            user.setSoftDelete(false);
            user.setImageName(userDefaultImage);
            User savedUser = userRepository.save(user);
            if(savedUser.getId() != null){
                System.out.println(savedUser);
                logger.info("User created successfully with email : {}",savedUser.getEmail());
            }
        } catch (Exception exception) {
            logger.error("Unexpected error occurred while creating user: {}", exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    @Transactional
    public void updateUserStatus(UUID userId, UserStatusUpdateDTO userStatusUpdateDTO) throws AccessDeniedException {
        logger.info("Start updating user status. User ID: {}, New Status: {}", userId, userStatusUpdateDTO.isActive());

        if(!isAdmin()){
            logger.warn("Unauthorized attempt to update user status. Only admins can update the status of users.");
            throw new AccessDeniedException("Only admins can activate or deactivate users.");
        }

        try {
            if(!userStatusUpdateDTO.isActive()){
                User activeUser = this.fetchUserById(userId);
                userRepository.updateUserStatusById(false, activeUser.getId());
                logger.info("User status updated successfully. User ID: {}, New Status: {}", userId, false);
            } else {
                User inActiveUser = userRepository.findInactiveUserById(userId).orElseThrow(() -> {
                    logger.warn("User with ID {} not found, Get user operation not performed", userId);
                    return new ResourceNotFoundException("User","ID",String.valueOf(userId),"Get User operation not performed");
                });
                userRepository.updateUserStatusById(true, inActiveUser.getId());
                logger.info("User status updated successfully. User ID: {}, New Status: {}", userId, true);
            }
        } catch (Exception exception) {
            logger.error("Unexpected error occurred while updating user status. User ID: {}, Error: {}", userId, exception.getMessage());
            throw exception;
        }
    }

    @Override
    @Transactional
    public void deleteUserById(UUID userId) throws AccessDeniedException {
        //Lambda Expression : Passing a function as an argument to another function
        //orElseThrow is a method that takes a Supplier as an argument. Supplier is a functional interface that accepts no parameters and returns a result. Here, the expected result is an exception.
        //Consumer is a functional interface in Java that takes an input and returns no result. Consumer is typically used for operations that take a parameter and return nothing (such as a print operation).
        //To use Consumer you must be sure that you want to perform an operation on an object and do not need to return a value.
        //For example, orElseThrow, which requires a Supplier, cannot use Consumer because its purpose is to create and return an exception.
        logger.info("Deleting user with ID : {}",userId);

       try{
           if(!this.isLoggedInUserMatching(userId) || this.isAdmin()) {
               User user = this.fetchUserById(userId);
               user.setSoftDelete(true);
               userRepository.save(user);
               logger.info("User with ID {} deleted successfully", user.getId());
           } else {
               logger.warn("Unauthorized attempt to delete another user's account. ");
               throw new AccessDeniedException("You can only delete your own account or if you're an admin.");
           }
       } catch (Exception exception) {
           logger.error("Unexpected error occurred while deleting user. User ID: {}, Error: {}", userId, exception.getMessage(), exception);
           throw exception;
       }
    }

    //Optional is a class that is used to prevent direct use of null and reduce problems related to NullPointerException.
    //This class is a container that may contain a value (which is present) or no value (which is empty).
    //Optional is a powerful tool for handling null values and makes code safer and more readable.
    //Correct use of Optional can help prevent NullPointerException and improve code readability and maintainability.
    //With Optional, you can easily check if a value exists and take the appropriate action.
    //Optional<User> optionalUser = userRepository.findById(userId);
    //Consumer<User> printUserDetails = foundUser -> System.out.println("User Found : " + foundUser);
    //optionalUser.ifPresent(printUserDetails);

    @Override
    public User fetchUserById(UUID userId) {
        logger.info("Fetching user with ID : {}",userId);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            logger.warn("User with ID {} not found, Get user operation not performed", userId);
            return new ResourceNotFoundException("User","ID",String.valueOf(userId),"Get User operation not performed");
        });
        logger.info("User found with ID : {}",user.getId());
        return user;
    }

    @Override
    public User fetchUserByEmail(String userEmail) {
        logger.info("Fetching user with Email : {}", userEmail);
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> {
            logger.warn("User with Email {} not found, Get user operation not performed", userEmail);
            return new ResourceNotFoundException("User","Email",String.valueOf(userEmail),"Get User operation not performed");
        });
        logger.info("User found with Email : {}",user.getId());
        return user;
    }

    private UserGetDto mapUserToDto(User user) {
        UserGetDto userGetDto = userMapper.toUserGetDto(user);
        boolean isFollowed = userRepository.existFollowedByCurrentUser(user.getId(), this.loggedInUserEmail());
        userGetDto.setFollowedByCurrentUser(isFollowed);
        return userGetDto;
    }

    @Override
    public UserGetDto getUserById(UUID userId) {
        User user = this.fetchUserById(userId);
        return userMapper.toUserGetDto(user);
    }

    @Override
    public UserGetDto getUserWithImage(UUID userId){
        User user = this.fetchUserById(userId);
        UserGetDto userGetDto = mapUserToDto(user);
        String userImage = imageService.downloadImage(user.getImageName(), userImagesBucket);
        userGetDto.setImage(userImage);
        return userGetDto;
    }

    @Override
    @Transactional
    public void savePost(UUID userId, Long postId) throws AccessDeniedException {
        logger.info("Saving post with ID: {} for user with ID: {}", postId, userId);

        User user = this.fetchUserById(userId);
        if(isLoggedInUserMatching(userId)){
            logger.warn("Unauthorized attempt to save the post from another user.");
            throw new AccessDeniedException("You can only save a post on your own behalf.");
        }

        try {
            Post post = postService.getPostById(postId);
            post.getSavedByUsers().add(user);
            postRepository.save(post);
            logger.info("Post with ID: {} saved successfully for user with ID: {}", postId, userId);
        } catch (Exception exception){
            logger.error("Error occurred while saving the post by the user. User ID: {} ,Post ID: {}, Error: {}", userId, postId, exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    @Transactional
    public void unSavePost(UUID userId, Long postId) throws AccessDeniedException {
        logger.info("UnSaving post with ID: {} for user with ID: {}", postId, userId);

        User user = this.fetchUserById(userId);
        if(isLoggedInUserMatching(userId)){
            logger.warn("Unauthorized attempt to unsave the post from another user.");
            throw new AccessDeniedException("You can only unsave a post on your own behalf.");
        }

        try {
            Post post = postService.getPostById(postId);
            post.getSavedByUsers().remove(user);
            postRepository.save(post);
            logger.info("Post with ID: {} unsaved successfully for user with ID: {}", postId, userId);
        } catch (Exception exception){
            logger.error("Error occurred while unsaved the post by the user. User ID: {} ,Post ID: {}, Error: {}", userId, postId, exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public List<UserGetDto> getFollowers(UUID userId) {
        logger.info("Fetching followers for user with ID: {}", userId);
        try {
            User user = this.fetchUserById(userId);
            List<UserGetDto> followers = user.getFollowers().stream()
                    .map(follower -> {
                        UserGetDto userGetDto = userMapper.toUserGetDto(follower);
                        String userImage = imageService.downloadImage(userGetDto.getImageName(), userImagesBucket);
                        userGetDto.setImage(userImage);
                        return userGetDto;
                    })
                    .collect(Collectors.toList());

            this.updateFollowedStatusForUsers(followers, this.loggedInUserEmail());

            logger.info("Retrieved {} followers for user with ID: {}", followers.size(), userId);
            return followers;
        } catch (Exception exception){
            logger.error("Error occurred while get followers for user. User ID: {} , Error: {}", userId, exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public List<UserGetDto> getFollowing(UUID userId) {
        logger.info("Fetching users followed by user with ID: {}", userId);
        try{
            User user = this.fetchUserById(userId);
            List<UserGetDto> followingUsers = user.getFollowing().stream()
                    .map(following -> {
                        UserGetDto userGetDto = userMapper.toUserGetDto(following);
                        String userImage = imageService.downloadImage(userGetDto.getImageName(), userImagesBucket);
                        userGetDto.setImage(userImage);
                        return userGetDto;
                    })
                    .collect(Collectors.toList());

            this.updateFollowedStatusForUsers(followingUsers, this.loggedInUserEmail());

            logger.info("Retrieved {} users followed by user with ID: {}", followingUsers.size(), userId);
            return followingUsers;
        } catch (Exception exception){
            logger.error("Error occurred while get following for user. User ID: {} , Error: {}", userId, exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    @Transactional
    public UserGetDto followUser(UUID userId, UUID followUserId) throws AccessDeniedException {
        logger.info("Attempting to follow user with ID: {} for user with ID: {}", followUserId, userId);
        User user = this.fetchUserById(userId);
        User followUser = this.fetchUserById(followUserId);

        if(isLoggedInUserMatching(userId)){
            logger.warn("Unauthorized attempt to follow another user with a different account.");
            throw new AccessDeniedException("You can only follow users with your own account.");
        }

        UserGetDto followUserGetDto;
        try {
            if(!user.getFollowing().contains(followUser)){
                user.getFollowing().add(followUser);
                followUser.getFollowers().add(user);
                user.setFollowingCount(user.getFollowingCount() + 1);
                followUser.setFollowersCount(followUser.getFollowersCount() + 1);
                userRepository.save(user);
                followUserGetDto = userMapper.toUserGetDto(userRepository.save(followUser));
                followUserGetDto.setFollowedByCurrentUser(true);
                logger.info("User with ID: {} successfully followed user with ID: {}", userId, followUserId);
            } else {
                logger.warn("User with ID: {} already follows user with ID: {}", userId, followUserId);
                followUserGetDto = userMapper.toUserGetDto(followUser);
                followUserGetDto.setFollowedByCurrentUser(true);
            }
            return followUserGetDto;
        } catch (Exception exception){
            logger.error("Error occurred while follow {} by {}, Error: {}", followUser, userId, exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    @Transactional
    public UserGetDto unfollowUser(UUID userId, UUID unfollowUserId) throws AccessDeniedException {
        logger.info("Attempting to unfollow user with ID: {} for user with ID: {}", unfollowUserId, userId);
        User user = this.fetchUserById(userId);
        User unfollowUser = this.fetchUserById(unfollowUserId);

        if(isLoggedInUserMatching(userId)){
            logger.warn("Unauthorized attempt to unfollow another user with a different account.");
            throw new AccessDeniedException("You can only unfollow users with your own account.");
        }

        UserGetDto unfollowUserGetDto;
        try {
            user.getFollowing().remove(unfollowUser);
            unfollowUser.getFollowers().remove(user);
            user.setFollowingCount(user.getFollowingCount() - 1);
            unfollowUser.setFollowersCount(unfollowUser.getFollowersCount() - 1);

            userRepository.save(user);
            unfollowUserGetDto = userMapper.toUserGetDto(userRepository.save(unfollowUser));
            unfollowUserGetDto.setFollowedByCurrentUser(false);
            logger.info("User with ID: {} successfully unfollowed user with ID: {}", userId, unfollowUserId);
            return unfollowUserGetDto;
        } catch (Exception exception){
            logger.error("Error occurred while unfollow {} by {}, Error: {}", unfollowUser, userId, exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public List<PostGetDto> getSavedPostsByUser(UUID userId) throws AccessDeniedException {
        logger.info("Fetching saved posts for user with ID: {}", userId);
        User user = this.fetchUserById(userId);

        if(isLoggedInUserMatching(userId)){
            logger.warn("Unauthorized attempt to view saved posts of another user.");
            throw new AccessDeniedException("You can only view your own saved posts.");
        }

        try{
            List<Post> savedPosts = new ArrayList<>(user.getSavedPosts());
            logger.info("Fetched {} saved posts for user with ID: {}", savedPosts.size(), userId);
            List<PostGetDto> postGetDtoList = postService.convertPostsToPostDtos(savedPosts);
            postService.updatePostsInteractionStatus(postGetDtoList, this.loggedInUserEmail());
            return postGetDtoList;
        } catch (Exception exception){
            logger.error("Error occurred while get saved post for user. User ID: {} , Error: {}", userId, exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public PaginatedResponse<UserBasicInfoDto> getAllBasicUserInfo(int pageNumber, int pageSize, String sortBy, String sortDir) {
        logger.info("Fetching all user basic info");

        Sort sort = SortHelper.getSortOrder(sortBy,sortDir);
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        try{
            Page<User> userPage = userRepository.findAll(pageable);

            List<User> users = userPage.getContent();
            List<UserBasicInfoDto> userGetDtoList = users.stream()
                    .map(userMapper::toUserBasicInfoDto) //Method Reference
                    .toList();

            PaginatedResponse<UserBasicInfoDto> paginatedResponse = new PaginatedResponse<>(
                    userGetDtoList,userPage.getSize(),userPage.getNumber(),userPage.getTotalPages(),userPage.getTotalElements(),userPage.isLast());
            logger.info("Total user basic info found : {}",users.size());
            return paginatedResponse;
        } catch (Exception exception) {
            logger.error("Error occurred while fetching all basic user info: {}", exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public UserBasicInfoDto getUserBasicInfoById(UUID userId) {
        logger.info("Fetching user with ID : {}", userId);
        User user = this.fetchUserById(userId);
        logger.info("User found with ID : {}",userId);
        UserBasicInfoDto userBasicInfoDto = userMapper.toUserBasicInfoDto(user);
        logger.info("UserBasicInfoDto created: {}", userBasicInfoDto);
        return userBasicInfoDto;
    }

    @Override
    @Transactional
    public void updateUser(UserUpdateDto userUpdateDto, UUID userId) throws AccessDeniedException {
        logger.info("Updating user with ID : {}",userId);

        if(isLoggedInUserMatching(userId) && !isAdmin()){
            logger.warn("Unauthorized attempt to update another user's account.");
            throw new AccessDeniedException("You can only update your own account.");
        }

        try{
            userRepository.findById(userId).map(user -> {
                user.setName(userUpdateDto.getName());
                user.setEmail(userUpdateDto.getEmail());
                user.setAbout(userUpdateDto.getAbout());
                user.setUserName(userUpdateDto.getUserName());
                user.setPhoneNumber(userUpdateDto.getPhoneNumber());
                User savedUser = userRepository.save(user);

                logger.info("User with ID {} updated successfully",userId);
                return savedUser;
            }).orElseThrow(() -> {
                logger.warn("User with ID {} not found, Updated user operation not performed", userId);
                return new ResourceNotFoundException("User","ID",String.valueOf(userId),"Update User operation not performed");
            });
        } catch (Exception exception){
            logger.error("Error occurred while updating user, Error: {}", exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public List<UserBasicInfoDto> searchUsersByUsernameOrName(String keyword) {
        logger.info("Fetching users followed by user with Username or Name: {}", keyword);
        try{
            List<User> users = this.userRepository.findAllByUserNameOrNameLike(keyword);

            List<UserBasicInfoDto> foundUsers = users.stream()
                    .map(user -> {
                        UserBasicInfoDto userBasicInfoDto = userMapper.toUserBasicInfoDto(user);
                        String userImage = imageService.downloadImage(user.getImageName(), userImagesBucket);
                        userBasicInfoDto.setImage(userImage);
                        return userBasicInfoDto;
                    })
                    .collect(Collectors.toList());

            logger.info("Retrieved {} users followed by user with Username and Name: {}", foundUsers.size(), keyword);
            return foundUsers;
        } catch (Exception exception){
            logger.error("Error occurred while get following for user. User Username or Name: {} Error: {}", keyword, exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public List<UserGetDto> suggestUsers() {
        logger.info("Fetching suggested users for current user with ID: {}", this.loggedInUserId());
        try{
            List<User> suggestedUsers = userRepository.findSuggestedUsers(this.loggedInUserId());

            List<UserGetDto> suggestedUsersList = suggestedUsers.stream()
                    .map(user -> {
                        UserGetDto userGetDto = userMapper.toUserGetDto(user);
                        String userImage = imageService.downloadImage(user.getImageName(), userImagesBucket);
                        userGetDto.setImage(userImage);
                        return userGetDto;
                    })
                    .collect(Collectors.toList());

            this.updateFollowedStatusForUsers(suggestedUsersList, this.loggedInUserEmail());

            logger.info("Retrieved {} suggested users for user with ID: {}", suggestedUsersList.size(), this.loggedInUserId());
            return suggestedUsersList;
        } catch (Exception exception){
            logger.error("Error occurred while fetching suggested users for current user with ID: {}. Error: {}", this.loggedInUserId(), exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public void setUserProfile(ImageData imageData, UUID userId) throws IOException {
        logger.info("Starting to set profile image for user with ID: {}", userId);
        String imageUrl;
        User user = this.fetchUserById(userId);

        if(isLoggedInUserMatching(userId)){
            logger.warn("Unauthorized attempt by user with ID: {} to set profile image for another user's account.", userId);
            throw new AccessDeniedException("You can only set profile image for your own account.");
        }

        try{
            if(imageData.base64Content() != null && !imageData.base64Content().isEmpty()){
                imageUrl = imageService.uploadImage(imageData, userImagesBucket);
                if (!user.getImageName().equals(userDefaultImage)){
                    imageService.deleteImage(user.getImageName(), userImagesBucket);
                }
                user.setImageName(imageUrl);
                userRepository.save(user);
            } else {
                logger.warn("No image content provided for user with ID: {}", userId);
            }
            logger.info("Profile image updated successfully for user with ID: {}", userId);
        } catch (Exception exception){
            logger.error("Error occurred while set profile image for user: {}, Error: {}", userId, exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public String loggedInUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @Override
    public UUID loggedInUserId() {
        return fetchUserByEmail(this.loggedInUserEmail()).getId();
    }

    @Override
    public boolean isLoggedInUserMatching(UUID userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUserEmail = authentication.getName();
        UUID loggedInUserId = userRepository.getUserIdByEmail(loggedInUserEmail);
        return !loggedInUserId.equals(userId);
    }

    @Override
    public boolean isAdmin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch((authority) -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    @Override
    public void updateFollowedStatusForUsers(List<UserGetDto> userGetDtoList, String userEmail){
        logger.info("Updating followed status for users for the logged-in user with email: {}", userEmail);
        List<UUID> followedUserIds = userRepository.findFollowedUserIdsByUser(userEmail);
        userGetDtoList.forEach(user -> user.setFollowedByCurrentUser(followedUserIds.contains(user.getId())));
    }

}
