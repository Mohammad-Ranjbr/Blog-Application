package com.blog_application.service;

import com.blog_application.dto.post.PostCreateDto;
import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.post.PostUpdateDto;
import com.blog_application.util.PostResponse;

import java.util.List;

public interface PostService {

    void deletePost(Long post_id);
    PostGetDto getPostById(Long postId);
    List<PostGetDto> getPostsByUser(Long userId);
    List<PostGetDto> searchPosts(String keyword);
    PostGetDto updatePost(PostUpdateDto postDto, Long postId);
    List<PostGetDto> getPostsByCategory(Long categoryId);
    List<PostGetDto> searchPostsWithQueryMethod(String keyword);
    PostGetDto createPost(PostCreateDto postCreateDto, Long userId, Long categoryId);
    PostResponse getAllPosts(int pageNumber, int pageSize,String sortBy,String sortDir);

}
