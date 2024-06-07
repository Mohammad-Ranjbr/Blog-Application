package com.blog_application.service.impl;

import com.blog_application.config.PostMapper;
import com.blog_application.dto.PostDto;
import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.Category;
import com.blog_application.model.Post;
import com.blog_application.model.User;
import com.blog_application.repository.CategoryRepository;
import com.blog_application.repository.PostRepository;
import com.blog_application.repository.UserRepository;
import com.blog_application.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public PostServiceImpl(PostMapper postMapper,PostRepository postRepository,UserRepository userRepository, CategoryRepository categoryRepository){
        this.postMapper =  postMapper;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }


    @Override
    public PostDto createPost(PostDto postDto,Long user_id,Long category_id) {
        User user = userRepository.findById(user_id).orElseThrow(() -> new ResourceNotFoundException("User","id",String.valueOf(user_id),"Get User not performed"));
        Category category = categoryRepository.findById(category_id).orElseThrow(() -> new ResourceNotFoundException("Category","id",String.valueOf(category_id),"Get Category not performed"));
        Post post = postMapper.toEntity(postDto);
        post.setUser(user);
        post.setCategory(category);
        post.setImageName("default.png");
        Post savedPost = postRepository.save(post);
        return postMapper.toDto(savedPost);
    }

    @Override
    public PostDto updatePost(PostDto post, Long post_id) {
        return null;
    }

    @Override
    public void deletePost(Long post_id) {

    }

    @Override
    public List<PostDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(postMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public PostDto getPostById(Long post_id) {
        Post post = postRepository.findById(post_id).orElseThrow(() -> new ResourceNotFoundException("Post","ID",String.valueOf(post_id),"Get Post not performed"));
        return postMapper.toDto(post);
    }

    @Override
    public List<PostDto> getPostsByUser(Long user_id) {
        User user = userRepository.findById(user_id).orElseThrow(() -> new ResourceNotFoundException("User","ID",String.valueOf(user_id),"Get User not performed"));
        List<Post> posts = postRepository.findAllByUser(user);
        return posts.stream().map(postMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<PostDto> getPostsByCategory(Long category_id) {
        Category category = categoryRepository.findById(category_id).orElseThrow(() -> new ResourceNotFoundException("Category","ID",String.valueOf(category_id),"Get Category not performed"));
        List<Post> posts = postRepository.findAllByCategory(category);
        return posts.stream().map(postMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<PostDto> searchPosts(String keyword) {
        return null;
    }

}
