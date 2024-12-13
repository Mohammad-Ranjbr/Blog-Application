package com.blog_application.controller;

import com.blog_application.dto.image.ImageResponseDto;
import com.blog_application.service.image.ImageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ImageController {

    @Value("${minio.post-bucket-name}")
    private String postImagesBucket;
    @Value("${minio.user-bucket-name}")
    private String userImagesBucket;
    private final ImageService imageService;
    private final static Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    public ImageController(ImageService imageService){
        this.imageService = imageService;
    }

    @GetMapping("/posts/image/{filename}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<ImageResponseDto> downloadPostImage(@PathVariable String filename) {
        logger.info("Received request to download post image: {}", filename);
        String postImage = imageService.downloadImage(filename, postImagesBucket);
        return new ResponseEntity<>(new ImageResponseDto(postImage), HttpStatus.OK);
    }

    @GetMapping("/users/image/{filename}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<ImageResponseDto> downloadUserImage(@PathVariable String filename) {
        logger.info("Received request to download user image: {}", filename);
        String userImage = imageService.downloadImage(filename, userImagesBucket);
        return new ResponseEntity<>(new ImageResponseDto(userImage), HttpStatus.OK);
    }

}
