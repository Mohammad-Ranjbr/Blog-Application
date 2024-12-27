package com.blog_application.service.impl.post;

import com.blog_application.config.mapper.post.PostMapper;

import com.blog_application.dto.image.ImageData;
import com.blog_application.dto.post.PostCreateDto;
import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.post.PostUpdateDto;
import com.blog_application.dto.tag.TagCreateDto;
import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.category.Category;
import com.blog_application.model.post.Post;
import com.blog_application.model.tag.Tag;
import com.blog_application.model.user.User;
import com.blog_application.repository.post.PostRepository;
import com.blog_application.repository.tag.TagRepository;
import com.blog_application.repository.user.UserRepository;
import com.blog_application.service.category.CategoryService;
import com.blog_application.service.comment.CommentService;
import com.blog_application.service.image.ImageService;
import com.blog_application.service.post.PostReactionService;
import com.blog_application.service.post.PostService;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class PostServiceImpl implements PostService {

    @Value("${post.default-image}")
    private String postDefaultPage;
    @Value("${minio.post-bucket-name}")
    private String postImagesBucket;
    @Value("${minio.user-bucket-name}")
    private String userImagesBucket;
    private final PostMapper postMapper;
    private final UserService userService;
    private final ImageService imageService;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentService commentService;
    private final CategoryService categoryService;
    private final PostReactionService postReactionService;
    private final static Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    @Autowired
    public PostServiceImpl(PostMapper postMapper, PostRepository postRepository,UserService userService,
                           CategoryService categoryService, UserRepository userRepository, @Lazy CommentService commentService,
                           TagRepository tagRepository, ImageService imageService, @Lazy PostReactionService postReactionService){
        this.postMapper =  postMapper;
        this.userService = userService;
        this.imageService = imageService;
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentService = commentService;
        this.categoryService = categoryService;
        this.postReactionService = postReactionService;
    }


    @Override
    @Transactional
    public PostGetDto createPost(PostCreateDto postCreateDto, UUID userId, Long categoryId) throws IOException {
        logger.info("Creating post with title : {}",postCreateDto.getTitle());

        User user = userService.fetchUserById(userId);
        Category category = categoryService.getCategoryById(categoryId);

        if(userService.isLoggedInUserMatching(user.getId())){
            logger.warn("Unauthorized attempt to create a post in another user's account.");
            throw new AccessDeniedException("You can only create posts in your own account.");
        }

       try{
           Post post = postMapper.toEntity(postCreateDto);
           post.setUser(user);
           post.setCategory(category);

           String imageUrl;
           ImageData imageData = postCreateDto.getImageData();
           if (imageData.base64Content() != null && !imageData.base64Content().isEmpty())  {
               imageUrl = imageService.uploadImage(imageData, postImagesBucket);
               logger.info("Image uploaded successfully for post: {}", postCreateDto.getTitle());
           } else {
               imageUrl = postDefaultPage;
           }
           post.setImageName(imageUrl);

           if (postCreateDto.getScheduledTime() != null && postCreateDto.getScheduledTime().isAfter(LocalDateTime.now())) { // The time is after now
               schedulePost(post, postCreateDto.getScheduledTime(), user);
               logger.info("Post scheduled successfully with title : {} at {}", postCreateDto.getTitle(), postCreateDto.getScheduledTime());
               return postMapper.toPostGetDto(post);
           } else {
               Post savedPost = postRepository.save(post);
               user.setPostsCount(user.getPostsCount() + 1);
               userRepository.save(user);
               logger.info("Post created successfully with title : {}", postCreateDto.getTitle());
               return postMapper.toPostGetDto(savedPost);
           }
       } catch (Exception exception){
           logger.error("Error occurred while creating post. User ID: {}, Error: {}", userId, exception.getMessage(), exception);
           throw exception;
       }
    }

    @Override
    @Transactional
    public PostGetDto updatePost(PostUpdateDto postUpdateDto, Long postId) throws AccessDeniedException {
        logger.info("Updating post with ID : {}",postId);

        Post p = postRepository.findById(postId).orElseThrow(() -> {
            logger.warn("Post with ID {} not found, Get post operation not performed",postId);
            return new ResourceNotFoundException("Post","ID",String.valueOf(postId),"Get Post operation not performed");
        });

        Category newCategory;
        if(postUpdateDto.getCategoryId() != null){
            newCategory = categoryService.getCategoryById(postUpdateDto.getCategoryId());
        } else {
            newCategory = p.getCategory();
        }

        if(userService.isLoggedInUserMatching(p.getUser().getId())){
            logger.warn("Unauthorized attempt to update a post in another user's account. ");
            throw new AccessDeniedException("You can only update own posts in your own account.");
        }

        try {
            Post updatedPost = postRepository.findById(postId).map(post -> {
                post.setCategory(newCategory);
                post.setTitle(postUpdateDto.getTitle());
                post.setContent(postUpdateDto.getContent());
                Post savedPost = postRepository.save(post);

                String imageUrl;
                ImageData imageData = postUpdateDto.getImageData();
                if (imageData.base64Content() != null && !imageData.base64Content().isEmpty())  {
                    try {
                        imageUrl = imageService.uploadImage(imageData, postImagesBucket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    post.setImageName(imageUrl);
                    logger.info("Image uploaded successfully for post: {}", postUpdateDto.getTitle());
                }

                logger.info("Post with ID {} updated successfully",postId);
                return savedPost;
            }).orElseThrow(() -> {
                logger.warn("Post with ID {} not found, Update post operation not performed",postId);
                return new ResourceNotFoundException("Post","ID",String.valueOf(postId),"Update Post operation not performed");
            });
            return postMapper.toPostGetDto(updatedPost);
        } catch (Exception exception){
            logger.error("Error occurred while updating post. Post ID: {}, Error: {}", postId, exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    @Transactional
    public void deletePost(Long postId) throws AccessDeniedException {
        logger.info("Deleting post with ID : {}",postId);
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            logger.warn("Post with ID {} not found, Delete post operation not performed",postId);
            return new ResourceNotFoundException("Post","ID",String.valueOf(postId),"Delete Post operation not performed");
        });
        User user = userService.fetchUserById(post.getUser().getId());

        try {
            if(!userService.isLoggedInUserMatching(post.getUser().getId()) || userService.isAdmin()){
                postRepository.delete(post);
                user.setPostsCount(user.getPostsCount() - 1);
                userRepository.save(user);
                logger.info("Post with ID {} deleted successfully",postId);
            } else {
                logger.warn("Unauthorized attempt to delete a post. ");
                throw new AccessDeniedException("You can only delete posts that you have created or if you are an admin.");
            }
        } catch (Exception exception){
            logger.error("Error occurred while removing post.  Post ID: {}, Error: {}", postId, exception.getMessage(), exception);
            throw exception;
        }

    }

    @Override
    public PaginatedResponse<PostGetDto> getAllPosts(int pageNumber, int pageSize, String sortBy, String sortDir) {
        logger.info("Fetching all posts with pageNumber: {}, pageSize: {}, sortBy: {}, sortDir: {}", pageNumber, pageSize, sortBy, sortDir);

        Sort sort = SortHelper.getSortOrder(sortBy,sortDir);
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        try{
            Page<Post> postPage = postRepository.findAll(pageable);

            List<Post> posts = postPage.getContent();
            List<PostGetDto> postGetDtoList = convertPostsToPostDtos(posts);

            this.updatePostsInteractionStatus(postGetDtoList, userService.loggedInUserEmail());

            PaginatedResponse<PostGetDto> paginatedResponse = new PaginatedResponse<>(
                    postGetDtoList,postPage.getSize(),postPage.getNumber(),postPage.getTotalPages(),postPage.getTotalElements(),postPage.isLast());
            logger.info("Total posts found : {}",postPage.getTotalElements());
            return paginatedResponse;
        } catch (Exception exception){
            logger.error("Error occurred while get all posts. Error: {}", exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public Post getPostById(Long postId) {
        logger.info("Fetching post with ID : {}",postId);
        return  postRepository.findById(postId).orElseThrow(() -> {
            logger.warn("Post with ID {} not found, Get post operation not performed",postId);
            return new ResourceNotFoundException("Post","ID",String.valueOf(postId),"Get Post operation not performed");
        });
    }

    @Override
    public PostGetDto getPostByIdWithImage(Long postId) {
        logger.info("Fetching post with ID : {}",postId);
        PostGetDto postGetDto = postMapper.toPostGetDto(getPostById(postId));
        this.updatePostInteractionStatus(postGetDto, userService.loggedInUserEmail());
        String postImage = imageService.downloadImage(postGetDto.getImageName(), postImagesBucket);
        String userImage = imageService.downloadImage(postGetDto.getUser().getImage(), userImagesBucket);
        postGetDto.setImage(postImage);
        postGetDto.getUser().setImage(userImage);
        return postGetDto;
    }

    @Override
    @Transactional
    public PostGetDto addTagToPost(Long postId, List<TagCreateDto> tagCreateDtos) throws AccessDeniedException {
        logger.info("Adding tags to post with ID: {}", postId);
        Post post = this.getPostById(postId);

        List<String> existingTagNames = new ArrayList<>(post.getTags().stream()
                .map(Tag::getName)
                .toList());

        if(userService.isLoggedInUserMatching(post.getUser().getId())){
            logger.warn("Unauthorized attempt to add tags to a post owned by another user.");
            throw new AccessDeniedException("You can only add tags to posts that you own.");
        }

        try{
            for (TagCreateDto tagCreateDto : tagCreateDtos) {
                logger.info("Processing tag: {}", tagCreateDto.getName());
                // Using orElseGet instead of orElse causes the new Tag object to be created only when needed
                // (when the tag is not found). orElse is always called, even if the value in Optional is present
                Tag tag = tagRepository.findByName(tagCreateDto.getName()).orElseGet(() ->
                        new Tag(tagCreateDto.getName(),
                                (tagCreateDto.getDescription()).isEmpty() ? tagCreateDto.getName().concat("  ").concat("Description") : tagCreateDto.getDescription())
                );
                // Check if the tag is already associated with the post
                if (!existingTagNames.contains(tag.getName())) {
                    post.getTags().add(tag);
                    existingTagNames.add(tag.getName()); // Update the list of tag names
                    logger.info("Tag ({}) added to post", tag.getName());
                } else {
                    logger.warn("Tag ({}) is already associated with post", tag.getName());
                }
            }

            // Because of CascadeType.PERSIST and CascadeType.MERGE, there is no need
            // to save tags manually This operation also automatically saves the tags
            Post savedPost = postRepository.save(post);
            logger.info("Post with ID {} with tags saved", postId);
            return postMapper.toPostGetDto(savedPost);
        } catch (Exception exception){
            logger.error("An error occurred while adding tags to post with ID {}. Error: {}", postId, exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    @Transactional
    public void removeTagsFromPost(Long postId, List<Long> tagIdsToRemove) throws AccessDeniedException {
        logger.info("Removing tags from post with ID {}", postId);
        Post post = this.getPostById(postId);

        if(userService.isLoggedInUserMatching(post.getUser().getId())){
            logger.warn("Unauthorized attempt to remove tags from a post owned by another user.");
            throw new AccessDeniedException("You can only remove tags from posts that you own.");
        }

        try {
            List<Tag> tagsToRemove = post.getTags().stream()
                    .filter(tag -> tagIdsToRemove.contains(tag.getId()))
                    .toList();
            logger.info("Tags to remove: {}", tagsToRemove);
            post.getTags().removeAll(tagsToRemove);
            postRepository.save(post);
            logger.info("Tags removed successfully from post with ID {}", postId);
        } catch (Exception exception){
            logger.error("An error occurred while removing tags from post with ID {}. Error: {}", postId, exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public PaginatedResponse<PostGetDto> getPostsByUser(UUID userId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        logger.info("Fetching posts for User with ID : {}",userId);
        User user = userService.fetchUserById(userId);

        Sort sort = SortHelper.getSortOrder(sortBy,sortDir);
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        try{
            Page<Post> postPage = postRepository.findAllByUser(user,pageable);

            List<Post> posts = postPage.getContent();
            List<PostGetDto> postGetDtoList = convertPostsToPostDtos(posts);

            this.updatePostsInteractionStatus(postGetDtoList, userService.loggedInUserEmail());

            PaginatedResponse<PostGetDto> paginatedResponse = new PaginatedResponse<>(
                    postGetDtoList,postPage.getSize(),postPage.getNumber(),postPage.getTotalPages(),postPage.getTotalElements(),postPage.isLast());
            logger.info("Total posts found for user with ID {} : {}",userId,posts.size());
            return paginatedResponse;
        } catch (Exception exception){
            logger.error("Error occurred while get posts for user. User ID: {}, Error: {}", userId, exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public PaginatedResponse<PostGetDto> getPostsByCategory(Long categoryId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        logger.info("Fetching posts for Category with ID : {}",categoryId);
        Category category = categoryService.getCategoryById(categoryId);

        Sort sort = SortHelper.getSortOrder(sortBy,sortDir);
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        try{
            Page<Post> postPage = postRepository.findAllByCategory(category,pageable);

            List<Post> posts = postPage.getContent();
            List<PostGetDto> postGetDtoList = convertPostsToPostDtos(posts);

            this.updatePostsInteractionStatus(postGetDtoList, userService.loggedInUserEmail());

            PaginatedResponse<PostGetDto> paginatedResponse = new PaginatedResponse<>(
                    postGetDtoList,postPage.getSize(),postPage.getNumber(),postPage.getTotalPages(),postPage.getTotalElements(),postPage.isLast());
            logger.info("Total posts found for category with ID {} : {}",categoryId,posts.size());
            return paginatedResponse;
        } catch (Exception exception){
            logger.error("Error occurred while get posts by category. Category ID: {}, Error: {}", categoryId, exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public PaginatedResponse<PostGetDto> searchPostsWithQueryMethod(String keyword, int pageNumber, int pageSize, String sortBy, String sortDir) {
        logger.info("Fetching posts with keyword: {}", keyword);

        Sort sort = SortHelper.getSortOrder(sortBy,sortDir);
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        try{
            Page<Post> postPage = postRepository.findByTitleContaining(keyword,pageable);

            List<Post> posts = postPage.getContent();
            List<PostGetDto> postGetDtoList =convertPostsToPostDtos(posts);

            this.updatePostsInteractionStatus(postGetDtoList, userService.loggedInUserEmail());

            PaginatedResponse<PostGetDto> paginatedResponse = new PaginatedResponse<>(
                    postGetDtoList,postPage.getSize(),postPage.getNumber(),postPage.getTotalPages(),postPage.getTotalElements(),postPage.isLast());
            logger.info("Fetched {} posts for keyword: {}", posts.size(), keyword);
            return paginatedResponse;
        } catch (Exception exception){
            logger.error("Error occurred while search post with query method. Keyword: {}, Error: {}", keyword, exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public PaginatedResponse<PostGetDto> searchPosts(String keyword, int pageNumber, int pageSize, String sortBy, String sortDir) {
        logger.info("Fetching posts with keyword: {}", keyword);

        Sort sort = SortHelper.getSortOrder(sortBy,sortDir);
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        try{
            Page<Post> postPage = postRepository.searchByTitle("%" + keyword + "%",pageable);

            List<Post> posts = postPage.getContent();
            List<PostGetDto> postGetDtoList = convertPostsToPostDtos(posts);

            this.updatePostsInteractionStatus(postGetDtoList, userService.loggedInUserEmail());

            PaginatedResponse<PostGetDto> paginatedResponse = new PaginatedResponse<>(
                    postGetDtoList,postPage.getSize(),postPage.getNumber(),postPage.getTotalPages(),postPage.getTotalElements(),postPage.isLast());
            logger.info("Fetched {} posts for keyword: {}", posts.size(), keyword);
            return paginatedResponse;
        } catch (Exception exception){
            logger.error("Error occurred while search post with keyword: {}, Error: {}", keyword, exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public List<PostGetDto> getHomePosts(){
        logger.info("Starting to fetch home posts for the logged-in user.");
        try {
            UUID userId = userService.loggedInUserId();
            String userEmail = userService.loggedInUserEmail();
            List<Post> posts = postRepository.findHomePosts(userId);
            List<PostGetDto> postGetDtoList = convertPostsToPostDtos(posts);
            this.updatePostsInteractionStatus(postGetDtoList, userEmail);
            logger.info("Fetched {} home posts for user ID: {}", posts.size(), userId);
            return postGetDtoList;
        } catch (Exception exception){
            logger.error("An error occurred while fetching home posts: {}", exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public List<PostGetDto> getExplorePosts() {
        logger.info("Starting to fetch explore posts for the logged-in user.");
        try {
            UUID userId = userService.loggedInUserId();
            String userEmail = userService.loggedInUserEmail();
            List<Post> posts = postRepository.findExplorePosts(userId);
            List<PostGetDto> postGetDtoList = convertPostsToPostDtos(posts);
            this.updatePostsInteractionStatus(postGetDtoList, userEmail);
            logger.info("Fetched {} explore posts for user ID: {}", posts.size(), userId);
            return postGetDtoList;
        } catch (Exception exception){
            logger.error("An error occurred while fetching explore posts: {}", exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public boolean checkIfSavedByCurrentUser(Long postId, String userEmail) {
        return postRepository.existSavedByCurrentUser(postId, userEmail);
    }

    @Override
    public void updateSavedStatusForPosts(List<PostGetDto> postGetDtos, String userEmail) {
        logger.info("Updating saved status for posts for user {}", userEmail);
        List<Long> savedPostIds = postRepository.findSavedPostIdsByUser(userEmail);
        postGetDtos.forEach(post -> {
            post.setSavedByCurrentUser(savedPostIds.contains(post.getId()));
            String userImage = imageService.downloadImage(post.getUser().getImage(), userImagesBucket);
            post.getUser().setImage(userImage);
        });
    }

    @Override
    public void updatePostInteractionStatus(PostGetDto postGetDto, String userEmail) {
        commentService.updateCommentsReactionStatus(postGetDto.getComments(), userEmail);
        boolean isLiked = postReactionService.checkIfLikedByCurrentUser(postGetDto.getId(), userEmail);
        postGetDto.setSavedByCurrentUser(this.checkIfSavedByCurrentUser(postGetDto.getId(), userEmail));
        postGetDto.setLikedByCurrentUser(isLiked);
    }

    @Override
    public void updatePostsInteractionStatus(List<PostGetDto> postGetDtoList, String userEmail) {
        postGetDtoList.forEach(post -> commentService.updateCommentsReactionStatus(post.getComments(), userEmail));
        postReactionService.updateLikedStatusForPosts(postGetDtoList, userService.loggedInUserEmail());
        this.updateSavedStatusForPosts(postGetDtoList, userService.loggedInUserEmail());
    }

    @Override
    public List<PostGetDto> convertPostsToPostDtos(List<Post> posts){
        return  posts.stream()
                .map(post -> {
                    PostGetDto postGetDto = postMapper.toPostGetDto(post);
                    String postImage = imageService.downloadImage(postGetDto.getImageName(), postImagesBucket);
                    postGetDto.setImage(postImage);
                    return postGetDto;
                })
                .toList();
    }


    private void schedulePost(Post schedulePost, LocalDateTime scheduledTime, User user) {
        // This method creates a ScheduledExecutorService instance with a single thread. This thread is responsible for scheduling and executing specified tasks.
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // This method of the Duration class calculates the time between the current time (LocalDateTime.now()) and the scheduled time (scheduledTime).
        // This method converts the calculated duration to milliseconds. This period of time indicates the delay until the execution of the task.
        long delay = Duration.between(LocalDateTime.now(), scheduledTime).toMillis();

        // TimeUnit.MILLISECONDS: The unit of delay time, which is milliseconds here.
        scheduler.schedule(() -> {
            postRepository.save(schedulePost);
            user.setPostsCount(user.getPostsCount() + 1);
            userRepository.save(user);
            logger.info("Notification: Scheduled post with title '{}' has been saved successfully.", schedulePost.getTitle());}, delay, TimeUnit.MILLISECONDS);
        scheduler.shutdown();
    }

    // ScheduledExecutorService ( java.util.concurrent ) is used to manage and execute scheduled tasks.
    // The ScheduledExecutorService allows you to run tasks in the future at a specified delay or periodically.
    // Execution of tasks with a certain delay: You can define a task that will be executed after a certain period of time.
    // Execution of tasks periodically: You can define a task to be executed regularly and at specified time intervals.
    // Management of threads (Threads): This interface automatically handles thread management, avoiding the manual creation and management of threads.
    // flexibility: You can schedule tasks on a delayed or periodic basis.
    // Sustainability: ScheduledExecutorService supports handling exceptions and preventing threads from stalling due to unexpected errors.

    // The Executors class in Java is part of the java.util.concurrent package and provides utility tools for creating and managing various executors.
    // Executors contains methods that briefly simplify the creation and configuration of executors. These tools include creating single-threaded, multi-threaded, and scheduled executions.
    // To execute tasks with specific timing : Executors.newScheduledThreadPool(int n)
    // Single Thread Executor: This type of executor executes tasks sequentially with a single thread. Suitable for tasks that must be performed in sequence without interference.
    // Fixed Thread Pool: This type of executor provides a fixed set of threads to execute tasks. Suitable for tasks that can be executed simultaneously, but the number of threads must be fixed.
    // Cached Thread Pool: This type of processor manages threads dynamically and can execute a large number of tasks simultaneously. Suitable for short-term and high-volume tasks.
    // Scheduled Thread Pool: This type of executor is used to execute scheduled tasks. Suitable for tasks that must be executed at specific times or periodically.
    // Using Executors in Java makes thread management simpler and more efficient. These tools allow you to run your tasks efficiently without having to manually manage threads.

    // Duration means duration and is included in Java programming as a class in the java.time package. This class is used to display and calculate the duration between two times (LocalDateTime, LocalTime or any other type of java.time).
    // This class allows you to perform operations such as comparing two times, calculating the duration between them, and performing various other actions on durations.
    // between(Temporal startInclusive, Temporal endExclusive): Used to calculate the duration between two times.
    // toDays(), toHours(), toMinutes(), toMillis(): Used to convert duration to different time units such as days, hours, minutes and milliseconds.
    // plus(Duration duration), minus(Duration duration): Used to add and subtract a Duration to another time.
    
}
