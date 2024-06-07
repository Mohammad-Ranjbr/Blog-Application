package com.blog_application.controller;

import com.blog_application.dto.PostDto;
import com.blog_application.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService){
        this.postService = postService;
    }

    @PostMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto, @PathVariable("userId") Long user_id, @PathVariable("categoryId") Long category_id){
        PostDto createdPost = postService.createPost(postDto,user_id,category_id);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

}
