package com.blog_application.service.impl;

import com.blog_application.dto.PostDto;
import com.blog_application.exception.ResourceNotFoundException;
import com.blog_application.model.Category;
import com.blog_application.model.Post;
import com.blog_application.model.User;
import com.blog_application.repository.CategoryRepository;
import com.blog_application.repository.PostRepository;
import com.blog_application.repository.UserRepository;
import com.blog_application.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final ModelMapper modelMapper;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public PostServiceImpl(ModelMapper modelMapper,PostRepository postRepository,UserRepository userRepository,CategoryRepository categoryRepository){
        this.modelMapper = modelMapper;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }


    @Override
    public PostDto createPost(PostDto postDto,Long user_id,Long category_id) {
        User user = userRepository.findById(user_id).orElseThrow(() -> new ResourceNotFoundException("User","id",String.valueOf(user_id),"Get User not performed"));
        Category category = categoryRepository.findById(category_id).orElseThrow(() -> new ResourceNotFoundException("Category","id",String.valueOf(category_id),"Get Category not performed"));
        Post post = this.postDtoToPost(postDto);
        post.setUser(user);
        post.setCategory(category);
        post.setImageName("default.png");
        Post savedPost = postRepository.save(post);
        return this.postToPostDto(savedPost);
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
        return null;
    }

    @Override
    public PostDto getPostById(Long post_id) {
        return null;
    }

    @Override
    public List<PostDto> getPostsByUser(Long user_id) {
        return null;
    }

    @Override
    public List<PostDto> getPostsByCategory(Long category_id) {
        return null;
    }

    @Override
    public List<PostDto> searchPosts(String keyword) {
        return null;
    }

    @Override
    public Post postDtoToPost(PostDto postDto) {
        return modelMapper.map(postDto,Post.class);
    }

    @Override
    public PostDto postToPostDto(Post post) {
        return modelMapper.map(post,PostDto.class);
    }

}
