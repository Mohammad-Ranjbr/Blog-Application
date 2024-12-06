package com.blog_application.service.impl.user;

import com.blog_application.config.mapper.post.PostMapper;
import com.blog_application.config.mapper.user.UserMapper;
import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.user.*;
import com.blog_application.exception.ResourceAlreadyExistsException;
import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.post.Post;
import com.blog_application.model.role.Role;
import com.blog_application.model.user.User;
import com.blog_application.repository.post.PostRepository;
import com.blog_application.repository.user.UserRepository;
import com.blog_application.service.post.PostService;
import com.blog_application.service.role.RoleService;
import com.blog_application.service.user.UserService;
import com.blog_application.util.responses.PaginatedResponse;
import com.blog_application.util.utils.SortHelper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final PostService postService;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, RoleService roleService
            ,@Lazy PostService postService, PostMapper postMapper,PostRepository postRepository,PasswordEncoder passwordEncoder){
        this.userMapper = userMapper;
        this.postMapper = postMapper;
        this.postService = postService;
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
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
    @Transactional
    public UserGetDto createUser(UserCreateDto userCreateDto) {
        logger.info("Creating user with username : {}",userCreateDto.getUserName());
        if(userRepository.existsByUserName(userCreateDto.getUserName())){
            logger.warn("User with Username {} already exists, Register user operation not performed", userCreateDto.getUserName());
            throw  new ResourceAlreadyExistsException("User","Username",String.valueOf(userCreateDto.getUserName()),"Register User operation not performed");
        } else if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            logger.warn("User with Email {} already exists, Register user operation not performed", userCreateDto.getEmail());
            throw  new ResourceAlreadyExistsException("User","Email",String.valueOf(userCreateDto.getEmail()),"Register User operation not performed");
        }
        Role userRole = roleService.getRoleByName("ROLE_USER");
        String hashPassword = passwordEncoder.encode(userCreateDto.getPassword());
        User user = userMapper.toEntity(userCreateDto);
        user.setPassword(hashPassword);
        user.setRoles(Set.of(userRole));
        user.setSoftDelete(false);
        User savedUser = userRepository.save(user);
        if(savedUser.getId() != null){
            System.out.println(savedUser);
            logger.info("User created successfully with email : {}",savedUser.getEmail());
        }
        return userMapper.toUserGetDto(savedUser);
    }

    @Override
    @Transactional
    public void updateUserStatus(UUID userId, UserStatusUpdateDTO userStatusUpdateDTO) {
        logger.info("Start updating user status. User ID: {}, New Status: {}", userId, userStatusUpdateDTO.isActive());
        if(!userStatusUpdateDTO.isActive()){
            try {
                User activeUser = this.fetchUserById(userId);
                userRepository.updateUserStatusById(false, activeUser.getId());
                logger.info("User status updated successfully. User ID: {}, New Status: {}", userId, false);
            } catch (Exception exception){
                logger.error("Error occurred while updating user status. User ID: {}, Error: {}", userId, exception.getMessage(), exception);
                throw exception;
            }
        } else {
            try{
                User inActiveUser = userRepository.findInactiveUserById(userId).orElseThrow(() -> {
                    logger.warn("User with ID {} not found, Get user operation not performed", userId);
                    return new ResourceNotFoundException("User","ID",String.valueOf(userId),"Get User operation not performed");
                });
                userRepository.updateUserStatusById(true, inActiveUser.getId());
                logger.info("User status updated successfully. User ID: {}, New Status: {}", userId, true);
            } catch (Exception exception){
                logger.error("Error occurred while updating user status. User ID: {}, Error: {}", userId, exception.getMessage(), exception);
                throw exception;
            }
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
        UUID loggedInUserId = this.getLoggedInUserId();
        if(!userId.equals(loggedInUserId)){
            logger.warn("Unauthorized attempt to delete another user's account. Logged-in user: {}, Target user: {}", loggedInUserId, userId);
            throw new AccessDeniedException("You can only delete your own account.");
        }
        User user = this.fetchUserById(userId);
        user.setSoftDelete(true);
        userRepository.save(user);
        logger.info("User with ID {} deleted successfully",user.getId());
    }

    @Override
    public User fetchUserById(UUID userId) {
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
        return user;
    }

    @Override
    public UserGetDto getUserById(UUID userId) {
        User user = this.fetchUserById(userId);
        return userMapper.toUserGetDto(user);
    }

    @Override
    @Transactional
    public void savePost(UUID userId, Long postId) {
        logger.info("Saving post with ID: {} for user with ID: {}", postId, userId);
        User user = this.fetchUserById(userId);
        Post post = postMapper.toEntity(postService.getPostById(postId));
        post.getSavedByUsers().add(user);
        postRepository.save(post);
        logger.info("Post with ID: {} saved successfully for user with ID: {}", postId, userId);
    }

    @Override
    @Transactional
    public void unSavePost(UUID userId, Long postId) {
        logger.info("UnSaving post with ID: {} for user with ID: {}", postId, userId);
        User user = this.fetchUserById(userId);
        Post post = postMapper.toEntity(postService.getPostById(postId));
        post.getSavedByUsers().remove(user);
        postRepository.save(post);
        logger.info("Post with ID: {} unsaved successfully for user with ID: {}", postId, userId);
    }

    @Override
    public List<UserGetDto> getFollowers(UUID userId) {
        logger.info("Fetching followers for user with ID: {}", userId);
        User user = this.fetchUserById(userId);
        List<UserGetDto> followers = user.getFollowers().stream()
                .map(userMapper::toUserGetDto)
                .collect(Collectors.toList());
        logger.info("Retrieved {} followers for user with ID: {}", followers.size(), userId);
        return followers;
    }

    @Override
    public List<UserGetDto> getFollowing(UUID userId) {
        logger.info("Fetching users followed by user with ID: {}", userId);
        User user = this.fetchUserById(userId);
        List<UserGetDto> followingUsers = user.getFollowing().stream()
                .map(userMapper::toUserGetDto)
                .collect(Collectors.toList());
        logger.info("Retrieved {} users followed by user with ID: {}", followingUsers.size(), userId);
        return followingUsers;
    }

    @Override
    @Transactional
    public void followUser(UUID userId, UUID followUserId) {
        logger.info("Attempting to follow user with ID: {} for user with ID: {}", followUserId, userId);
        User user = this.fetchUserById(userId);
        User followUser = this.fetchUserById(followUserId);

        if(!user.getFollowing().contains(followUser)){
            user.getFollowing().add(followUser);
            followUser.getFollowers().add(user);
            user.setFollowingCount(user.getFollowingCount() + 1);
            followUser.setFollowersCount(followUser.getFollowersCount() + 1);
            userRepository.save(user);
            userRepository.save(followUser);
            logger.info("User with ID: {} successfully followed user with ID: {}", userId, followUserId);
        } else {
            logger.warn("User with ID: {} already follows user with ID: {}", userId, followUserId);
        }
    }

    @Override
    @Transactional
    public void unfollowUser(UUID userId, UUID unfollowUserId) {
        logger.info("Attempting to unfollow user with ID: {} for user with ID: {}", unfollowUserId, userId);
        User user = this.fetchUserById(userId);
        User unfollowUser = this.fetchUserById(unfollowUserId);

        user.getFollowing().remove(unfollowUser);
        unfollowUser.getFollowers().remove(user);
        user.setFollowingCount(user.getFollowingCount() - 1);
        unfollowUser.setFollowersCount(unfollowUser.getFollowersCount() - 1);

        userRepository.save(user);
        userRepository.save(unfollowUser);
        logger.info("User with ID: {} successfully unfollowed user with ID: {}", userId, unfollowUserId);
    }

    @Override
    public List<PostGetDto> getSavedPostsByUser(UUID userId) {
        logger.info("Fetching saved posts for user with ID: {}", userId);
        User user = this.fetchUserById(userId);
        List<Post> savedPosts = new ArrayList<>(user.getSavedPosts());
        logger.info("Fetched {} saved posts for user with ID: {}", savedPosts.size(), userId);
        return savedPosts.stream()
                .map(postMapper::toPostGetDto)
                .collect(Collectors.toList());
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
        User user = this.fetchUserById(userId);
        logger.info("User found with ID : {}",userId);
        UserBasicInfoDto userBasicInfoDto = userMapper.toUserBasicInfoDto(user);
        logger.info("UserBasicInfoDto created: {}", userBasicInfoDto);
        return userBasicInfoDto;
    }

    @Override
    @Transactional
    public UserGetDto updateUser(UserUpdateDto userUpdateDto, UUID userId) throws AccessDeniedException {
        logger.info("Updating user with ID : {}",userId);
        UUID loggedInUserId = this.getLoggedInUserId();
        if(!userId.equals(loggedInUserId)) {
            logger.warn("Unauthorized attempt to update another user's account. Logged-in user: {}, Target user: {}", loggedInUserId, userId);
            throw new AccessDeniedException("You can only update your own account.");
        }
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

    @Override
    public UUID getLoggedInUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = authentication.getName();
        return userRepository.getUserIdByEmail(loggedInEmail);
    }

}
