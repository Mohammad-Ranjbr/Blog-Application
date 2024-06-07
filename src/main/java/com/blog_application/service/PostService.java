package com.blog_application.service;

import com.blog_application.model.Post;

import java.util.List;

public interface PostService {

    Post createPost(Post post);
    Post updatePost(Post post,Long post_id);
    void deletePost(Long post_id);
    List<Post> getAllPosts();
    Post getPostById(Long post_id);
    List<Post> getPostsByUser(Long user_id);
    List<Post> getPostsByCategory(Long category_id);
    List<Post> searchPosts(String keyword);

}
