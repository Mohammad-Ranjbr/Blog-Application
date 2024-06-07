package com.blog_application.service.impl;

import com.blog_application.model.Post;
import com.blog_application.service.PostService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Override
    public Post createPost(Post post) {
        return null;
    }

    @Override
    public Post updatePost(Post post, Long post_id) {
        return null;
    }

    @Override
    public void deletePost(Long post_id) {

    }

    @Override
    public List<Post> getAllPosts() {
        return null;
    }

    @Override
    public Post getPostById(Long post_id) {
        return null;
    }

    @Override
    public List<Post> getPostsByUser(Long user_id) {
        return null;
    }

    @Override
    public List<Post> getPostsByCategory(Long category_id) {
        return null;
    }

    @Override
    public List<Post> searchPosts(String keyword) {
        return null;
    }
    
}
