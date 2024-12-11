package com.blog_application.service.minio;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface MinioService {

    String uploadFile(String fileName, InputStream inputStream, Long contentLength, String contentType)  throws IOException;
    InputStream downloadFile(String fileName);

}
