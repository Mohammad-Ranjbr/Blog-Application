package com.blog_application.controller;

import com.blog_application.dto.post.PostCreateDto;
import com.blog_application.dto.post.PostGetDto;
import com.blog_application.dto.post.PostUpdateDto;
import com.blog_application.dto.post.reaction.PostReactionRequestDto;
import com.blog_application.dto.tag.TagCreateDto;
import com.blog_application.service.minio.MinioService;
import com.blog_application.service.post.PostReactionService;
import com.blog_application.service.post.PostService;
import com.blog_application.util.responses.ApiResponse;
import com.blog_application.util.constants.ApplicationConstants;
import com.blog_application.util.responses.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Post Controller")
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final MinioService minioService;
    private final PostReactionService postReactionService;
    private final static Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    public PostController(PostService postService,PostReactionService postReactionService, MinioService minioService){
        this.postService = postService;
        this.minioService = minioService;
        this.postReactionService = postReactionService;
    }

    //POST Mapping-Create Post
    @PostMapping("/user/{userId}/category/{categoryId}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    @Operation(summary = "Create Post Rest Api", description = "Create Post Rest Api is used save post into database")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",description = "Http Status 201 CREATED")
    public ResponseEntity<PostGetDto> createPost(@Valid @RequestBody PostCreateDto postCreateDto, @PathVariable("userId") UUID userId,
                                                 @PathVariable("categoryId") Long categoryId) throws IOException {
        logger.info("Received request to create post for user with ID : {} and category with ID : {}", userId, categoryId);
        PostGetDto createdPost = postService.createPost(postCreateDto, userId, categoryId);
        logger.info("Returning response for post creation with title: {}", createdPost.getTitle());
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    //GET Mapping-Get All Posts By User
    @GetMapping("/user/{userId}")
    @SecurityRequirement(name = "Jwt Token Authentication")
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
    @SecurityRequirement(name = "Jwt Token Authentication")
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
    @SecurityRequirement(name = "Jwt Token Authentication")
    @Operation(summary = "Get All Posts Rest Api", description = "Get All Post Rest Api is used to fetch all the posts from the database")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "Http Status 201 SUCCESS")
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
    @SecurityRequirement(name = "Jwt Token Authentication")
    @Operation(summary = "Get Post Rest Api", description = "Get Post By Id Rest Api is used to get single post from database")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "Http Status 201 SUCCESS")
    public ResponseEntity<PostGetDto> getPostById(@PathVariable("id") Long postId){
        logger.info("Received request to fetch post with ID : {}", postId);
        PostGetDto postDto = postService.getPostById(postId);
        logger.info("Returning response for post with ID : {}", postId);
        return new ResponseEntity<>(postDto,HttpStatus.OK);
    }

    //DELETE Mapping-Delete Post
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Jwt Token Authentication")
    @Operation(summary = "Delete Post Rest Api", description = "Delete Post Rest Api is used to delete a particular post from the database")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "Http Status 201 SUCCESS")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable("id") Long postId) throws AccessDeniedException {
        logger.info("Received request to delete post with ID : {}", postId);
        postService.deletePost(postId);
        logger.info("Returning response for delete post with ID : {}", postId);
        return new ResponseEntity<>(new ApiResponse("Post Deleted Successfully",true),HttpStatus.OK);
    }

    //PUT Mapping-Update Post
    @PutMapping("/{id}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    @Operation(summary = "Update Post Rest Api", description = "Update Post Rest Api is used to update a particular post in the database")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "Http Status 201 SUCCESS")
    public ResponseEntity<PostGetDto> updatePost(@Valid @RequestBody PostUpdateDto postUpdateDto, @PathVariable("id") Long postId) throws AccessDeniedException {
        logger.info("Received request to update post with ID : {}", postId);
        PostGetDto updatedPost = postService.updatePost(postUpdateDto,postId);
        logger.info("Returning response for updated post with ID : {}", postId);
        return new ResponseEntity<>(updatedPost,HttpStatus.OK);
    }

    //GET Mapping-Search Post
    @GetMapping("/search/{keyword}")
    @SecurityRequirement(name = "Jwt Token Authentication")
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
    @SecurityRequirement(name = "Jwt Token Authentication")
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

    @PostMapping("/like")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<PostGetDto> likePost(@RequestBody PostReactionRequestDto requestDto) throws AccessDeniedException {
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
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<PostGetDto> addTagToPost(@PathVariable("post_id") Long postId,@RequestBody List<TagCreateDto> tagCreateDtos) throws AccessDeniedException {
        logger.info("Received request to add tags to post with ID: {}", postId);
        PostGetDto postGetDto = postService.addTagToPost(postId,tagCreateDtos);
        logger.info("Tags added successfully to post with ID: {}", postId);
        return new ResponseEntity<>(postGetDto,HttpStatus.OK);
    }

    @DeleteMapping("/{post_id}/tags")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<ApiResponse> removeTagsFromPost(@PathVariable("post_id") Long postId,@RequestBody List<Long> tagIds) throws AccessDeniedException {
        logger.info("Received request to remove tags from post with ID: {}", postId);
        postService.removeTagsFromPost(postId,tagIds);
        logger.info("Tags removed successfully from post with ID: {}", postId);
        return new ResponseEntity<>(new ApiResponse("Tags Deleted From Post Successfully",true),HttpStatus.OK);
    }

//    @PostMapping("/upload-image")
//    @SecurityRequirement(name = "Jwt Token Authentication")
//    public ResponseEntity<ApiResponse> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
//        logger.info("Received request to upload image: {}", file.getOriginalFilename());
//        if(!file.isEmpty()){
//            String url = minioService.uploadFile(file);
//            logger.info("Image uploaded successfully: {}", file.getOriginalFilename());
//            return new ResponseEntity<>(new ApiResponse(url, true), HttpStatus.OK);
//        } else {
//            logger.warn("Failed to upload image: file is empty.");
//            return new ResponseEntity<>(new ApiResponse("File must not be empty. Please upload a valid file.", false), HttpStatus.BAD_REQUEST);
//        }
//    }

    @GetMapping("/download-image/{filename}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<InputStreamResource> downloadImage(@PathVariable String filename) {
        logger.info("Received request to download image: {}", filename);
        try {
            InputStream inputStream = minioService.downloadFile(filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(new InputStreamResource(inputStream));
        } catch (Exception exception) {
            logger.error("Error occurred while downloading image: {}, Error: {}", filename, exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
