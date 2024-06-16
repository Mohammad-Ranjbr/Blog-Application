package com.blog_application.service.impl;

import com.blog_application.config.mapper.PostMapper;
import com.blog_application.dto.PostDto;
import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.Category;
import com.blog_application.model.Post;
import com.blog_application.model.User;
import com.blog_application.repository.CategoryRepository;
import com.blog_application.repository.PostRepository;
import com.blog_application.repository.UserRepository;
import com.blog_application.service.PostService;
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
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final static Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    @Autowired
    public PostServiceImpl(PostMapper postMapper,PostRepository postRepository,UserRepository userRepository, CategoryRepository categoryRepository){
        this.postMapper =  postMapper;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }


    @Override
    public PostDto createPost(PostDto postDto,Long userId,Long categoryId) {
        logger.info("Creating post with title : {}",postDto.getTitle());
        User user = userRepository.findById(userId).orElseThrow(() -> {
            logger.warn("User with ID {} not found, Get user operation not performed", userId);
            return new ResourceNotFoundException("User","id",String.valueOf(userId),"Get User operation not performed");
        });
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
            logger.warn("Category with ID {} not found, Get category operation not performed",categoryId);
            return new ResourceNotFoundException("Category","id",String.valueOf(categoryId),"Get Category operation not performed");
        });
        Post post = postMapper.toEntity(postDto);
        post.setUser(user);
        post.setCategory(category);
        post.setImageName("default.png");
        Post savedPost = postRepository.save(post);
        logger.info("Post created successfully with title : {}",postDto.getTitle());
        return postMapper.toDto(savedPost);
    }

    @Override
    public PostDto updatePost(PostDto postDto, Long postId) {
        logger.info("Updating post with ID : {}",postId);
        Post updatedPost = postRepository.findById(postId).map(post -> {
            post.setTitle(postDto.getTitle());
            post.setContent(postDto.getContent());
            post.setImageName(postDto.getImageName());
            Post savedPost = postRepository.save(post);
            logger.info("Post with ID {} updated successfully",postId);
            return savedPost;
        }).orElseThrow(() -> {
            logger.warn("Post with ID {} not found, Update post operation not performed",postId);
            return new ResourceNotFoundException("Post","ID",String.valueOf(postId),"Update Post operation not performed");
        });
        return postMapper.toDto(updatedPost);
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
        List<PostDto> postDtoList = posts.stream().map(postMapper::toDto).toList();
        PostResponse postResponse = new PostResponse();
        postResponse.setContent(postDtoList);
        postResponse.setPageNumber(postPage.getNumber());
        postResponse.setPageSize(postPage.getSize());
        postResponse.setTotalPages(postPage.getTotalPages());
        postResponse.setTotalElements(postPage.getTotalElements());
        postResponse.setLastPage(postResponse.isLastPage());
        logger.info("Total posts found : {}",postPage.getTotalElements());
        return postResponse;
    }

    @Override
    public PostDto getPostById(Long postId) {
        logger.info("Fetching post with ID : {}",postId);
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            logger.warn("Post with ID {} not found, Get post operation not performed",postId);
            return new ResourceNotFoundException("Post","ID",String.valueOf(postId),"Get Post operation not performed");
        });
        logger.info("Post found with ID : {}",postId);
        return postMapper.toDto(post);
    }

    @Override
    public List<PostDto> getPostsByUser(Long userId) {
        logger.info("Fetching posts for User with ID : {}",userId);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            logger.warn("User with ID {} not found, delete user operation not performed", userId);
            return new ResourceNotFoundException("User","ID",String.valueOf(userId),"Get User operation not performed");
        });
        List<Post> posts = postRepository.findAllByUser(user);
        logger.info("Total posts found for user ID {}: {}",userId,posts.size());
        return posts.stream().map(postMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<PostDto> getPostsByCategory(Long categoryId) {
        logger.info("Fetching posts for Category with ID : {}",categoryId);
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
            logger.warn("Category with ID {} not found, get category operation not performed",categoryId);
            return new ResourceNotFoundException("Category","ID",String.valueOf(categoryId),"Get Category operation not performed");
        });
        List<Post> posts = postRepository.findAllByCategory(category);
        logger.info("Total posts found for category ID {}: {}",categoryId,posts.size());
        return posts.stream().map(postMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<PostDto> searchPostsWithQueryMethod(String keyword) {
        logger.info("Fetching posts with keyword: {}", keyword);
        List<Post> posts = postRepository.findByTitleContaining(keyword);
        List<PostDto> postDtos = posts.stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
        logger.info("Fetched {} posts for keyword: {}", postDtos.size(), keyword);
        return postDtos;
    }

    @Override
    public List<PostDto> searchPosts(String keyword) {
        logger.info("Fetching posts with keyword: {}", keyword);
        List<Post> posts = postRepository.searchByTitle("%" + keyword + "%");
        List<PostDto> postDtos = posts.stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
        logger.info("Fetched {} posts for keyword: {}", postDtos.size(), keyword);
        return postDtos;
    }

}
