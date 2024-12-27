package com.blog_application.service.impl.minio;

import com.blog_application.service.minio.MinioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
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

    @Override
    public String uploadFile(String bucketName, String fileName, InputStream inputStream, Long contentLength, String contentType) {
        logger.info("Received request to upload file: {}", fileName);

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

    @Override
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

    @Override
    public void deleteFile(String bucketName, String fileName) {
        logger.info("Received request to delete file: {}", fileName);
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build());
            logger.info("File deleted successfully: {}", fileName);
        } catch (Exception exception){
            logger.error("Error occurred while deleting file: {}, Error: {}", fileName, exception.getMessage(), exception);
            throw new RuntimeException("Error deleting file", exception);
        }
    }

}
