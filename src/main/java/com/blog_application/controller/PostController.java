package com.blog_application.controller;

import com.blog_application.dto.post.PostCreateDto;
import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.post.PostUpdateDto;
import com.blog_application.dto.post.reaction.PostReactionRequestDto;
import com.blog_application.dto.tag.TagCreateDto;
import com.blog_application.service.post.PostReactionService;
import com.blog_application.service.post.PostService;
import com.blog_application.util.responses.ApiResponse;
import com.blog_application.util.constants.ApplicationConstants;
import com.blog_application.util.responses.PaginatedResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final PostReactionService postReactionService;
    private final static Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    public PostController(PostService postService,PostReactionService postReactionService){
        this.postService = postService;
        this.postReactionService = postReactionService;
    }

    //POST Mapping-Create Post
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/user/{userId}/category/{categoryId}")
    @SecurityRequirement(name = "Bear Authentication")
    public ResponseEntity<PostGetDto> createPost(@Valid @RequestBody PostCreateDto postCreateDto, @PathVariable("userId") UUID userId, @PathVariable("categoryId") Long categoryId){
        logger.info("Received request to create post for user with ID : {} and category with ID : {}", userId, categoryId);
        PostGetDto createdPost = postService.createPost(postCreateDto,userId,categoryId);
        logger.info("Returning response for post creation with title: {}", createdPost.getTitle());
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    //GET Mapping-Get All Posts By User
    @GetMapping("/user/{userId}")
    public ResponseEntity<PaginatedResponse<PostGetDto>> getPostsByUser(@PathVariable("userId") UUID userId,
            @RequestParam(value = ApplicationConstants.PAGE_NUMBER,defaultValue = ApplicationConstants.DEFAULT_PAGE_NUMBER,required = false) int pageNumber,
            @RequestParam(value = ApplicationConstants.PAGE_SIZE,defaultValue = ApplicationConstants.DEFAULT_PAGE_SIZE,required = false) int pageSize,
            @RequestParam(value = ApplicationConstants.SORT_BY,defaultValue = ApplicationConstants.DEFAULT_POST_SORT_BY,required = false) String sortBy,
            @RequestParam(value = ApplicationConstants.SORT_DIR,defaultValue = ApplicationConstants.DEFAULT_SORT_DIR,required = false) String sortDir){

        logger.info("Received request to get posts for user with ID : {}", userId);
        PaginatedResponse<PostGetDto> posts = postService.getPostsByUser(userId,pageNumber,pageSize,sortBy,sortDir);
        logger.info("Returning response for get posts for user with ID : {}", userId);
        return new ResponseEntity<>(posts,HttpStatus.OK);
    }

    //GET Mapping-Get All Posts By Category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PaginatedResponse<PostGetDto>> getPostsByCategory(@PathVariable("categoryId") Long categoryId,
            @RequestParam(value = ApplicationConstants.PAGE_NUMBER,defaultValue = ApplicationConstants.DEFAULT_PAGE_NUMBER,required = false) int pageNumber,
            @RequestParam(value = ApplicationConstants.PAGE_SIZE,defaultValue = ApplicationConstants.DEFAULT_PAGE_SIZE,required = false) int pageSize,
            @RequestParam(value = ApplicationConstants.SORT_BY,defaultValue = ApplicationConstants.DEFAULT_POST_SORT_BY,required = false) String sortBy,
            @RequestParam(value = ApplicationConstants.SORT_DIR,defaultValue = ApplicationConstants.DEFAULT_SORT_DIR,required = false) String sortDir){

        logger.info("Received request to get posts for category with ID : {}", categoryId);
        PaginatedResponse<PostGetDto> posts = postService.getPostsByCategory(categoryId,pageNumber,pageSize,sortBy,sortDir);
        logger.info("Returning response for posts for category with ID : {}", categoryId);
        return new ResponseEntity<>(posts,HttpStatus.OK);
    }

    //GET Mapping-Get All Posts
    @GetMapping("/")
    public ResponseEntity<PaginatedResponse<PostGetDto>> getAllPosts(
            @RequestParam(value = ApplicationConstants.PAGE_NUMBER,defaultValue = ApplicationConstants.DEFAULT_PAGE_NUMBER,required = false) int pageNumber,
            @RequestParam(value = ApplicationConstants.PAGE_SIZE,defaultValue = ApplicationConstants.DEFAULT_PAGE_SIZE,required = false) int pageSize,
            @RequestParam(value = ApplicationConstants.SORT_BY,defaultValue = ApplicationConstants.DEFAULT_POST_SORT_BY,required = false) String sortBy,
            @RequestParam(value = ApplicationConstants.SORT_DIR,defaultValue = ApplicationConstants.DEFAULT_SORT_DIR,required = false) String sortDir){

        logger.info("Received request to fetch all posts");
        PaginatedResponse<PostGetDto> postResponse = postService.getAllPosts(pageNumber,pageSize,sortBy,sortDir);
        logger.info("Returning response for all posts");
        return new ResponseEntity<>(postResponse,HttpStatus.OK);
    }

    //GET Mapping-Get Post By ID
    @GetMapping("/{id}")
    public ResponseEntity<PostGetDto> git (@PathVariable("id") Long postId){
        logger.info("Received request to fetch post with ID : {}", postId);
        PostGetDto postDto = postService.getPostById(postId);
        logger.info("Returning response for post with ID : {}", postId);
        return new ResponseEntity<>(postDto,HttpStatus.OK);
    }

    //DELETE Mapping-Delete Post
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bear Authentication")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable("id") Long postId){
        logger.info("Received request to delete post with ID : {}", postId);
        postService.deletePost(postId);
        logger.info("Returning response for delete post with ID : {}", postId);
        return new ResponseEntity<>(new ApiResponse("Post Deleted Successfully",true),HttpStatus.OK);
    }

    //PUT Mapping-Update Post
    @PutMapping("/{id}")
    public ResponseEntity<PostGetDto> updatePost(@Valid @RequestBody PostUpdateDto postUpdateDto, @PathVariable("id") Long postId){
        logger.info("Received request to update post with ID : {}", postId);
        PostGetDto updatedPost = postService.updatePost(postUpdateDto,postId);
        logger.info("Returning response for updated post with ID : {}", postId);
        return new ResponseEntity<>(updatedPost,HttpStatus.OK);
    }

    //GET Mapping-Search Post
    @GetMapping("/search/{keyword}")
    public ResponseEntity<PaginatedResponse<PostGetDto>> searchPostByTitle(@PathVariable("keyword") String title,
              @RequestParam(value = ApplicationConstants.PAGE_NUMBER,defaultValue = ApplicationConstants.DEFAULT_PAGE_NUMBER,required = false) int pageNumber,
              @RequestParam(value = ApplicationConstants.PAGE_SIZE,defaultValue = ApplicationConstants.DEFAULT_PAGE_SIZE,required = false) int pageSize,
              @RequestParam(value = ApplicationConstants.SORT_BY,defaultValue = ApplicationConstants.DEFAULT_POST_SORT_BY,required = false) String sortBy,
              @RequestParam(value = ApplicationConstants.SORT_DIR,defaultValue = ApplicationConstants.DEFAULT_SORT_DIR,required = false) String sortDir){

        logger.info("Received request to search posts by title containing : {}", title);
        PaginatedResponse<PostGetDto> posts = postService.searchPosts(title,pageNumber,pageSize,sortBy,sortDir);
        logger.info("Returning response for search posts by title containing : {}", title);
        return new ResponseEntity<>(posts,HttpStatus.OK);
    }

    //GET Mapping-Search Post
    @GetMapping("/search2/{keyword}")
    public ResponseEntity<PaginatedResponse<PostGetDto>> searchPostByTitleWithQueryMethod(@PathVariable("keyword") String title,
             @RequestParam(value = ApplicationConstants.PAGE_NUMBER,defaultValue = ApplicationConstants.DEFAULT_PAGE_NUMBER,required = false) int pageNumber,
             @RequestParam(value = ApplicationConstants.PAGE_SIZE,defaultValue = ApplicationConstants.DEFAULT_PAGE_SIZE,required = false) int pageSize,
             @RequestParam(value = ApplicationConstants.SORT_BY,defaultValue = ApplicationConstants.DEFAULT_POST_SORT_BY,required = false) String sortBy,
             @RequestParam(value = ApplicationConstants.SORT_DIR,defaultValue = ApplicationConstants.DEFAULT_SORT_DIR,required = false) String sortDir){

        logger.info("Received request to search posts by title using query method with keyword : {}", title);
        PaginatedResponse<PostGetDto> posts = postService.searchPostsWithQueryMethod(title,pageNumber,pageSize,sortBy,sortDir);
        logger.info("Returning response for search posts by title using query method with keyword : {}", title);
        return new ResponseEntity<>(posts,HttpStatus.OK);
    }

    //OPTIONS Mapping for all posts
    @RequestMapping(value = "/",method = RequestMethod.OPTIONS)
    public ResponseEntity<?> optionsForAllPosts(){
        logger.info("Received OPTIONS request for all posts");
        HttpHeaders headers = new HttpHeaders();
        headers.add(ApplicationConstants.HEADER_ALLOW,"GET,POST,OPTIONS");
        logger.info("Returning response with allowed methods for all posts");
        return new ResponseEntity<>(headers,HttpStatus.OK);
    }

    //OPTIONS Mapping fo single post
    @RequestMapping(value = "/{id}",method = RequestMethod.OPTIONS)
    public ResponseEntity<?> optionsForSinglePost(@PathVariable("id") Long postId){
        logger.info("Received OPTIONS request for post with ID : {}", postId);
        ResponseEntity<?> response = ResponseEntity.ok()
                .allow(HttpMethod.GET,HttpMethod.PUT,HttpMethod.DELETE,HttpMethod.OPTIONS)
                .build();
        logger.info("Returning response with allowed methods for post with ID : {}", postId);
        return response;
    }

    @PostMapping("/like")
    public ResponseEntity<PostGetDto> likePost(@RequestBody PostReactionRequestDto requestDto){
        logger.info("Received like request for User {} on Post {}", requestDto.getUserId(), requestDto.getPostId());
        PostGetDto postGetDto = postReactionService.likePost(requestDto);
        if(postGetDto == null){
            logger.warn("No response DTO generated for like post request");
            return ResponseEntity.noContent().build();
        }
        logger.info("Returning response for like post with User {} on Post {}", requestDto.getUserId(), requestDto.getPostId());
        return new ResponseEntity<>(postGetDto,HttpStatus.OK);
    }

    @PostMapping("/{post_id}/tags")
    public ResponseEntity<PostGetDto> addTagToPost(@PathVariable("post_id") Long postId,@RequestBody List<TagCreateDto> tagCreateDtos){
        logger.info("Received request to add tags to post with ID: {}", postId);
        PostGetDto postGetDto = postService.addTagToPost(postId,tagCreateDtos);
        logger.info("Tags added successfully to post with ID: {}", postId);
        return new ResponseEntity<>(postGetDto,HttpStatus.OK);
    }

    @DeleteMapping("/{post_id}/tags")
    public ResponseEntity<ApiResponse> removeTagsFromPost(@PathVariable("post_id") Long postId,@RequestBody List<Long> tagIds){
        logger.info("Received request to remove tags from post with ID: {}", postId);
        postService.removeTagsFromPost(postId,tagIds);
        logger.info("Tags removed successfully from post with ID: {}", postId);
        return new ResponseEntity<>(new ApiResponse("Tags Deleted From Post Successfully",true),HttpStatus.OK);
    }

}
