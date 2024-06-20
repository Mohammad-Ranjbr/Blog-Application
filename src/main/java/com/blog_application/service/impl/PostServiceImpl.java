package com.blog_application.service.impl;

import com.blog_application.config.mapper.CategoryMapper;
import com.blog_application.config.mapper.PostMapper;
import com.blog_application.config.mapper.UserMapper;
import com.blog_application.dto.post.PostCreateDto;
//import com.blog_application.dto.post.PostDto;
import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.post.PostUpdateDto;
import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.Category;
import com.blog_application.model.Post;
import com.blog_application.model.User;
import com.blog_application.repository.PostRepository;
import com.blog_application.service.CategoryService;
import com.blog_application.service.PostService;
import com.blog_application.service.UserService;
import com.blog_application.util.PostResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final UserService userService;
    private final CategoryMapper categoryMapper;
    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final static Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    @Autowired
    public PostServiceImpl(PostMapper postMapper,PostRepository postRepository,UserService userService,
                           CategoryService categoryService,UserMapper userMapper,CategoryMapper categoryMapper){
        this.userMapper = userMapper;
        this.postMapper =  postMapper;
        this.userService = userService;
        this.categoryMapper = categoryMapper;
        this.postRepository = postRepository;
        this.categoryService = categoryService;
    }


    @Override
    public PostGetDto createPost(PostCreateDto postCreateDto, UUID userId, Long categoryId) {
        logger.info("Creating post with title : {}",postCreateDto.getTitle());
        User user = userMapper.toEntity(userService.getUserById(userId));
        Category category = categoryMapper.toEntity(categoryService.getCategoryById(categoryId));
        Post post = postMapper.toEntity(postCreateDto);
        post.setUser(user);
        post.setCategory(category);
        post.setImageName("default.png");
        Post savedPost = postRepository.save(post);
        logger.info("Post created successfully with title : {}",postCreateDto.getTitle());
        return postMapper.toPostGetDto(savedPost);
    }

    @Override
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
    public PostResponse getAllPosts(int pageNumber, int pageSize,String sortBy,String sortDir) {
        logger.info("Fetching all posts with pageNumber: {}, pageSize: {}, sortBy: {}, sortDir: {}", pageNumber, pageSize, sortBy, sortDir);
        Sort sort = (sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Post> postPage = postRepository.findAll(pageable);
        List<Post> posts = postPage.getContent();
        List<PostGetDto> postGetDtoList = posts.stream().map(postMapper::toPostGetDto).toList();
        PostResponse postResponse = new PostResponse();
        postResponse.setContent(postGetDtoList);
        postResponse.setPageNumber(postPage.getNumber());
        postResponse.setPageSize(postPage.getSize());
        postResponse.setTotalPages(postPage.getTotalPages());
        postResponse.setTotalElements(postPage.getTotalElements());
        postResponse.setLastPage(postResponse.isLastPage());
        logger.info("Total posts found : {}",postPage.getTotalElements());
        return postResponse;
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
    public List<PostGetDto> getPostsByUser(UUID userId) {
        logger.info("Fetching posts for User with ID : {}",userId);
        User user = userMapper.toEntity(userService.getUserById(userId));
        List<Post> posts = postRepository.findAllByUser(user);
        logger.info("Total posts found for user with ID {} : {}",userId,posts.size());
        return posts.stream()
                .map(postMapper::toPostGetDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostGetDto> getPostsByCategory(Long categoryId) {
        logger.info("Fetching posts for Category with ID : {}",categoryId);
        Category category = categoryMapper.toEntity(categoryService.getCategoryById(categoryId));
        List<Post> posts = postRepository.findAllByCategory(category);
        logger.info("Total posts found for category with ID {} : {}",categoryId,posts.size());
        return posts.stream()
                .map(postMapper::toPostGetDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostGetDto> searchPostsWithQueryMethod(String keyword) {
        logger.info("Fetching posts with keyword: {}", keyword);
        List<Post> posts = postRepository.findByTitleContaining(keyword);
        List<PostGetDto> postDtos = posts.stream()
                .map(postMapper::toPostGetDto)
                .collect(Collectors.toList());
        logger.info("Fetched {} posts for keyword: {}", postDtos.size(), keyword);
        return postDtos;
    }

    @Override
    public List<PostGetDto> searchPosts(String keyword) {
        logger.info("Fetching posts with keyword: {}", keyword);
        List<Post> posts = postRepository.searchByTitle("%" + keyword + "%");
        List<PostGetDto> postDtos = posts.stream()
                .map(postMapper::toPostGetDto)
                .collect(Collectors.toList());
        logger.info("Fetched {} posts for keyword: {}", postDtos.size(), keyword);
        return postDtos;
    }

}
