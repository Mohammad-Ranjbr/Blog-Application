package com.blog_application.controller;

import com.blog_application.service.minio.MinioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@RequestMapping("/api/v1")
public class ImageController {

    @Value("${minio.post-bucket-name}")
    private String postImagesBucket;
    @Value("${minio.user-bucket-name}")
    private String userImagesBucket;
    private final MinioService minioService;
    private final static Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    public ImageController(MinioService minioService){
        this.minioService = minioService;
    }

    @GetMapping("/posts/image/{filename}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<InputStreamResource> downloadPostImage(@PathVariable String filename) {
        logger.info("Received request to download post image: {}", filename);
        try {
            InputStream inputStream = minioService.downloadFile(postImagesBucket, filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(new InputStreamResource(inputStream));
        } catch (Exception exception) {
            logger.error("Error occurred while downloading post image: {}, Error: {}", filename, exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/users/image/{filename}")
    @SecurityRequirement(name = "Jwt Token Authentication")
    public ResponseEntity<InputStreamResource> downloadUserImage(@PathVariable String filename) {
        logger.info("Received request to download user image: {}", filename);
        try {
            InputStream inputStream = minioService.downloadFile(userImagesBucket, filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(new InputStreamResource(inputStream));
        } catch (Exception exception) {
            logger.error("Error occurred while downloading user image: {}, Error: {}", filename, exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
