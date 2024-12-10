package com.blog_application.service.impl.minio;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Service
public class MinioService {

    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.bucket-name}")
    private String bucketName;
    private final S3Client s3Client;
    private final static Logger logger = LoggerFactory.getLogger(MinioService.class);

    @Autowired
    public MinioService(S3Client s3Client){
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        logger.info("Received request to upload image: {}", multipartFile.getOriginalFilename());

        try {
            String fileExtension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
            String randomImageName = UUID.randomUUID().toString().concat(".").concat(Objects.requireNonNull(fileExtension));
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(randomImageName)
                    .contentType(multipartFile.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));
            logger.info("Image uploaded successfully. Filename: {}", randomImageName);
            return endpoint + "/" + bucketName + "/" + randomImageName;
        } catch (Exception exception){
            logger.error("Error occurred while uploading image. Filename: {}, Error: {}", multipartFile.getOriginalFilename(), exception.getMessage(), exception);
            throw exception;
        }
    }

    public InputStream downloadFile(String fileName){
        logger.info("Initiating download for file: {}", fileName);
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            InputStream inputStream = s3Client.getObject(request);
            logger.info("File downloaded successfully: {}", fileName);
            return inputStream;
        } catch (Exception exception){
            logger.error("Error occurred while downloading file: {}, Error: {}", fileName, exception.getMessage(), exception);
            throw new RuntimeException("Error downloading file", exception);
        }
    }

}
