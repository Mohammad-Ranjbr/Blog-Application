package com.blog_application.controller;

import com.blog_application.dto.PostDto;
import com.blog_application.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService){
        this.postService = postService;
    }

    //POST Mapping-Create Post
    @PostMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto, @PathVariable("userId") Long user_id, @PathVariable("categoryId") Long category_id){
        PostDto createdPost = postService.createPost(postDto,user_id,category_id);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    //GET Mapping-Get All Posts By User
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDto>> getPostsByUser(@PathVariable("userId") Long user_id){
        List<PostDto> posts = postService.getPostsByUser(user_id);
        return new ResponseEntity<>(posts,HttpStatus.OK);
    }

    //GET Mapping-Get All Posts By Category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PostDto>> getPostsByCategory(@PathVariable("categoryId") Long category_id){
        List<PostDto> posts = postService.getPostsByCategory(category_id);
        return new ResponseEntity<>(posts,HttpStatus.OK);
    }

    //GET Mapping-Get All Posts
    @GetMapping("/")
    public ResponseEntity<List<PostDto>> getAllPosts(@RequestParam(value = "pageNumber",defaultValue = "0",required = false) int pageNumber,
                                                     @RequestParam(value = "pageSize",defaultValue = "5",required = false) int pageSize){
        List<PostDto> posts = postService.getAllPosts(pageNumber,pageSize);
        return new ResponseEntity<>(posts,HttpStatus.OK);
    }

    //GET Mapping-Get Post By ID
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> git (@PathVariable("id") Long post_id){
        PostDto postDto = postService.getPostById(post_id);
        return new ResponseEntity<>(postDto,HttpStatus.OK);
    }

}
