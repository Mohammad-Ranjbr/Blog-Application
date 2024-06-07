package com.blog_application.service;

import com.blog_application.dto.PostDto;
import com.blog_application.util.PostResponse;

import java.util.List;

public interface PostService {

    PostDto createPost(PostDto postDto,Long user_id,Long category_id);
    PostDto updatePost(PostDto postDto,Long post_id);
    void deletePost(Long post_id);
   PostResponse getAllPosts(int pageNumber, int pageSize);
    PostDto getPostById(Long post_id);
    List<PostDto> getPostsByUser(Long user_id);
    List<PostDto> getPostsByCategory(Long category_id);
    List<PostDto> searchPosts(String keyword);

}
