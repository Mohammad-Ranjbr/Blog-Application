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
import com.blog_application.util.PostResponse;
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
    public PostDto updatePost(PostDto postDto, Long post_id) {
        Post updatedPost = postRepository.findById(post_id).map(post -> {
            post.setTitle(postDto.getTitle());
            post.setContent(postDto.getContent());
            post.setImageName(postDto.getImageName());
            Post savedPost = postRepository.save(post);
            return savedPost;
        }).orElseThrow(() -> new ResourceNotFoundException("Post","ID",String.valueOf(post_id),"Get Post not performed"));
        return postMapper.toDto(updatedPost);
    }

    @Override
    public void deletePost(Long post_id) {
        Post post = postRepository.findById(post_id).orElseThrow(() -> new ResourceNotFoundException("Post","ID",String.valueOf(post_id),"Get Post not performed"));
        postRepository.delete(post);
    }

    @Override
    public PostResponse getAllPosts(int pageNumber, int pageSize,String sortBy,String sortDir) {
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
        return postResponse;
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
    public List<PostDto> searchPostsWithQueryMethod(String keyword) {
        List<Post> posts = postRepository.findByTitleContaining(keyword);
        return posts.stream().map(postMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<PostDto> searchPosts(String keyword) {
        List<Post> posts = postRepository.searchByTitle("%" + keyword + "%");
        return posts.stream().map(postMapper::toDto).collect(Collectors.toList());
    }

}
