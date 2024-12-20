package com.blog_application.service.impl.minio;

import com.blog_application.service.minio.MinioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

@Service
public class MinioServiceImpl implements MinioService {

    private final S3Client s3Client;
    private final static Logger logger = LoggerFactory.getLogger(MinioServiceImpl.class);

    @Autowired
    public MinioServiceImpl(S3Client s3Client){
        this.s3Client = s3Client;
    }

    public String uploadFile(String bucketName, String fileName, InputStream inputStream, Long contentLength, String contentType) {
        logger.info("Received request to upload image: {}", fileName);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, contentLength));
            return fileName;
        } catch (Exception exception) {
            logger.error("Error occurred while uploading file. Filename: {}, Error: {}", fileName, exception.getMessage(), exception);
            throw new RuntimeException("File upload failed", exception);
        }
    }

    public InputStream downloadFile(String bucketName, String fileName){
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
