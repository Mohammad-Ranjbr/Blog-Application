package com.blog_application.controller;

import com.blog_application.dto.PostDto;
import com.blog_application.service.PostService;
import com.blog_application.util.ApiResponse;
import com.blog_application.util.PostResponse;
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
    public ResponseEntity<PostResponse> getAllPosts(@RequestParam(value = "pageNumber",defaultValue = "0",required = false) int pageNumber,
                                                    @RequestParam(value = "pageSize",defaultValue = "10",required = false) int pageSize,
                                                    @RequestParam(value = "sortBy",defaultValue = "title",required = false) String sortBy,
                                                    @RequestParam(value = "sortDir",defaultValue = "asc",required = false) String sortDir){
        PostResponse postResponse = postService.getAllPosts(pageNumber,pageSize,sortBy,sortDir);
        return new ResponseEntity<>(postResponse,HttpStatus.OK);
    }

    //GET Mapping-Get Post By ID
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> git (@PathVariable("id") Long post_id){
        PostDto postDto = postService.getPostById(post_id);
        return new ResponseEntity<>(postDto,HttpStatus.OK);
    }

    //DELETE Mapping-Delete Post
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable("id") Long post_id){
        postService.deletePost(post_id);
        return new ResponseEntity<>(new ApiResponse("Post Deleted Successfully",true),HttpStatus.OK);
    }

    //PUT Mapping-Update Post
    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@RequestBody PostDto postDto,@PathVariable("id") Long post_id){
        PostDto updatedPost = postService.updatePost(postDto,post_id);
        return new ResponseEntity<>(updatedPost,HttpStatus.OK);
    }

}
