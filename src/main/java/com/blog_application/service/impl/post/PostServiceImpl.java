package com.blog_application.service.impl.post;

import com.blog_application.config.mapper.category.CategoryMapper;
import com.blog_application.config.mapper.post.PostMapper;

import com.blog_application.config.mapper.user.UserMapper;
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
import com.blog_application.service.category.CategoryService;
import com.blog_application.service.post.PostService;
import com.blog_application.service.user.UserService;
import com.blog_application.util.responses.PaginatedResponse;
import com.blog_application.util.utils.SortHelper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class PostServiceImpl implements PostService {

    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final UserService userService;
    private final TagRepository tagRepository;
    private final CategoryMapper categoryMapper;
    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final static Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    @Autowired
    public PostServiceImpl(PostMapper postMapper,PostRepository postRepository,UserService userService,
                           CategoryService categoryService,UserMapper userMapper,CategoryMapper categoryMapper,TagRepository tagRepository){
        this.userMapper = userMapper;
        this.postMapper =  postMapper;
        this.userService = userService;
        this.tagRepository = tagRepository;
        this.categoryMapper = categoryMapper;
        this.postRepository = postRepository;
        this.categoryService = categoryService;
    }


    @Override
    @Transactional
    public PostGetDto createPost(PostCreateDto postCreateDto, UUID userId, Long categoryId) {
        logger.info("Creating post with title : {}",postCreateDto.getTitle());

        User user = userMapper.toEntity(userService.getUserById(userId));
        Category category = categoryMapper.toEntity(categoryService.getCategoryById(categoryId));

        Post post = postMapper.toEntity(postCreateDto);
        post.setUser(user);
        post.setCategory(category);
        post.setImageName("default.png");

        if (postCreateDto.getScheduledTime() != null && postCreateDto.getScheduledTime().isAfter(LocalDateTime.now())) { // The time is after now
            schedulePost(post, postCreateDto.getScheduledTime());
            logger.info("Post scheduled successfully with title : {} at {}", postCreateDto.getTitle(), postCreateDto.getScheduledTime());
            return postMapper.toPostGetDto(post);
        } else {
            Post savedPost = postRepository.save(post);
            logger.info("Post created successfully with title : {}", postCreateDto.getTitle());
            return postMapper.toPostGetDto(savedPost);
        }
    }

    @Override
    @Transactional
    public PostGetDto updatePost(PostUpdateDto postUpdateDto, Long postId) {
        logger.info("Updating post with ID : {}",postId);
        Post updatedPost = postRepository.findById(postId).map(post -> {
            post.setTitle(postUpdateDto.getTitle());
            post.setContent(postUpdateDto.getContent());
            post.setImageName(postUpdateDto.getImageName());
            Post savedPost = postRepository.save(post);
            logger.info("Post with ID {} updated successfully",postId);
            return savedPost;
        }).orElseThrow(() -> {
            logger.warn("Post with ID {} not found, Update post operation not performed",postId);
            return new ResourceNotFoundException("Post","ID",String.valueOf(postId),"Update Post operation not performed");
        });
        return postMapper.toPostGetDto(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        logger.info("Deleting post with ID : {}",postId);
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            logger.warn("Post with ID {} not found, Delete post operation not performed",postId);
            return new ResourceNotFoundException("Post","ID",String.valueOf(postId),"Delete Post operation not performed");
        });
        postRepository.delete(post);
        logger.info("Post with ID {} deleted successfully",postId);
    }

    @Override
    public PaginatedResponse<PostGetDto> getAllPosts(int pageNumber, int pageSize, String sortBy, String sortDir) {
        logger.info("Fetching all posts with pageNumber: {}, pageSize: {}, sortBy: {}, sortDir: {}", pageNumber, pageSize, sortBy, sortDir);

        Sort sort = SortHelper.getSortOrder(sortBy,sortDir);
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Post> postPage = postRepository.findAll(pageable);

        List<Post> posts = postPage.getContent();
        List<PostGetDto> postGetDtoList = posts.stream().map(postMapper::toPostGetDto).toList();

        PaginatedResponse<PostGetDto> paginatedResponse = new PaginatedResponse<>(
                postGetDtoList,postPage.getSize(),postPage.getNumber(),postPage.getTotalPages(),postPage.getTotalElements(),postPage.isLast());
        logger.info("Total posts found : {}",postPage.getTotalElements());
        return paginatedResponse;
    }

    @Override
    public PostGetDto getPostById(Long postId) {
        logger.info("Fetching post with ID : {}",postId);
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            logger.warn("Post with ID {} not found, Get post operation not performed",postId);
            return new ResourceNotFoundException("Post","ID",String.valueOf(postId),"Get Post operation not performed");
        });
        logger.info("Post found with ID : {}",postId);
        return postMapper.toPostGetDto(post);
    }

    @Override
    @Transactional
    public PostGetDto addTagToPost(Long postId, List<TagCreateDto> tagCreateDtos) {
        logger.info("Adding tags to post with ID: {}", postId);
        Post post = postMapper.toEntity(this.getPostById(postId));
        for (TagCreateDto tagCreateDto : tagCreateDtos) {
            logger.info("Processing tag: {}", tagCreateDto.getName());
            // Using orElseGet instead of orElse causes the new Tag object to be created only when needed
            // (when the tag is not found). orElse is always called, even if the value in Optional is present
            Tag tag = tagRepository.findByName(tagCreateDto.getName()).orElseGet(() ->
                new Tag(tagCreateDto.getName(),
                        (tagCreateDto.getDescription()).isEmpty() ? tagCreateDto.getName().concat("  ").concat("Description") : tagCreateDto.getDescription())
            );
            post.getTags().add(tag);
            logger.info("Tag ({}) added to post", tag.getName());
        }
        // Because of CascadeType.PERSIST and CascadeType.MERGE, there is no need
        // to save tags manually This operation also automatically saves the tags
        Post savedPost = postRepository.save(post);
        logger.info("Post with ID {} with tags saved", postId);
        return postMapper.toPostGetDto(savedPost);
    }

    @Override
    public PaginatedResponse<PostGetDto> getPostsByUser(UUID userId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        logger.info("Fetching posts for User with ID : {}",userId);
        User user = userMapper.toEntity(userService.getUserById(userId));

        Sort sort = SortHelper.getSortOrder(sortBy,sortDir);
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Post> postPage = postRepository.findAllByUser(user,pageable);

        List<Post> posts = postPage.getContent();
        List<PostGetDto> postGetDtoList = posts.stream()
                .map(postMapper::toPostGetDto)
                .toList();

        PaginatedResponse<PostGetDto> paginatedResponse = new PaginatedResponse<>(
                postGetDtoList,postPage.getSize(),postPage.getNumber(),postPage.getTotalPages(),postPage.getTotalElements(),postPage.isLast());
        logger.info("Total posts found for user with ID {} : {}",userId,posts.size());
        return paginatedResponse;
    }

    @Override
    public PaginatedResponse<PostGetDto> getPostsByCategory(Long categoryId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        logger.info("Fetching posts for Category with ID : {}",categoryId);
        Category category = categoryMapper.toEntity(categoryService.getCategoryById(categoryId));

        Sort sort = SortHelper.getSortOrder(sortBy,sortDir);
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Post> postPage = postRepository.findAllByCategory(category,pageable);

        List<Post> posts = postPage.getContent();
        List<PostGetDto> postGetDtoList = posts.stream()
                        .map(postMapper::toPostGetDto)
                .toList();

        PaginatedResponse<PostGetDto> paginatedResponse = new PaginatedResponse<>(
                postGetDtoList,postPage.getSize(),postPage.getNumber(),postPage.getTotalPages(),postPage.getTotalElements(),postPage.isLast());
        logger.info("Total posts found for category with ID {} : {}",categoryId,posts.size());
        return paginatedResponse;
    }

    @Override
    public PaginatedResponse<PostGetDto> searchPostsWithQueryMethod(String keyword, int pageNumber, int pageSize, String sortBy, String sortDir) {
        logger.info("Fetching posts with keyword: {}", keyword);

        Sort sort = SortHelper.getSortOrder(sortBy,sortDir);
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Post> postPage = postRepository.findByTitleContaining(keyword,pageable);

        List<Post> posts = postPage.getContent();
        List<PostGetDto> postGetDtoList = posts.stream()
                .map(postMapper::toPostGetDto)
                .toList();

        PaginatedResponse<PostGetDto> paginatedResponse = new PaginatedResponse<>(
                postGetDtoList,postPage.getSize(),postPage.getNumber(),postPage.getTotalPages(),postPage.getTotalElements(),postPage.isLast());
        logger.info("Fetched {} posts for keyword: {}", posts.size(), keyword);
        return paginatedResponse;
    }

    @Override
    public PaginatedResponse<PostGetDto> searchPosts(String keyword, int pageNumber, int pageSize, String sortBy, String sortDir) {
        logger.info("Fetching posts with keyword: {}", keyword);

        Sort sort = SortHelper.getSortOrder(sortBy,sortDir);
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Post> postPage = postRepository.searchByTitle("%" + keyword + "%",pageable);

        List<Post> posts = postPage.getContent();
        List<PostGetDto> postGetDtoList = posts.stream()
                .map(postMapper::toPostGetDto)
                .toList();

        PaginatedResponse<PostGetDto> paginatedResponse = new PaginatedResponse<>(
                postGetDtoList,postPage.getSize(),postPage.getNumber(),postPage.getTotalPages(),postPage.getTotalElements(),postPage.isLast());
        logger.info("Fetched {} posts for keyword: {}", posts.size(), keyword);
        return paginatedResponse;
    }

    private void schedulePost(Post schedulePost, LocalDateTime scheduledTime) {
        // This method creates a ScheduledExecutorService instance with a single thread. This thread is responsible for scheduling and executing specified tasks.
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // This method of the Duration class calculates the time between the current time (LocalDateTime.now()) and the scheduled time (scheduledTime).
        // This method converts the calculated duration to milliseconds. This period of time indicates the delay until the execution of the task.
        long delay = Duration.between(LocalDateTime.now(), scheduledTime).toMillis();

        // TimeUnit.MILLISECONDS: The unit of delay time, which is milliseconds here.
        scheduler.schedule(() -> {
            postRepository.save(schedulePost);
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
