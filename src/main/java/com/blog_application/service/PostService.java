package com.blog_application.service;

import com.blog_application.dto.post.PostDto;
import com.blog_application.dto.post.PostGetDto;
import com.blog_application.util.PostResponse;

import java.util.List;

public interface PostService {

    void deletePost(Long post_id);
    PostGetDto getPostById(Long postId);
    List<PostGetDto> getPostsByUser(Long userId);
    List<PostGetDto> searchPosts(String keyword);
    PostDto updatePost(PostDto postDto,Long postId);
    List<PostDto> getPostsByCategory(Long categoryId);
    List<PostDto> searchPostsWithQueryMethod(String keyword);
    PostDto createPost(PostDto postDto,Long userId,Long categoryId);
    PostResponse getAllPosts(int pageNumber, int pageSize,String sortBy,String sortDir);

}
