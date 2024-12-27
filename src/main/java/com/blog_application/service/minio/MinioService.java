package com.blog_application.service.minio;

import java.io.IOException;
import java.io.InputStream;

public interface MinioService {

    String uploadFile(String bucketName, String fileName, InputStream inputStream, Long contentLength, String contentType)  throws IOException;
    InputStream downloadFile(String bucketName, String fileName);
    void deleteFile(String bucketName, String fileName);

}
