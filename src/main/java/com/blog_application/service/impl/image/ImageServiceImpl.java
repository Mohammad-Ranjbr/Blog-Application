package com.blog_application.service.impl.image;

import com.blog_application.dto.image.ImageData;
import com.blog_application.service.image.ImageService;
import com.blog_application.service.minio.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    private final MinioService minioService;

    @Autowired
    public ImageServiceImpl(MinioService minioService){
        this.minioService = minioService;
    }

    @Override
    public String uploadImage(ImageData imageData, String bucketName) throws IOException {
        if (imageData != null && imageData.base64Content() != null && !imageData.base64Content().isEmpty()) {
            byte[] decodedBytes = Base64.getDecoder().decode(imageData.base64Content());
            InputStream imageInputStream = new ByteArrayInputStream(decodedBytes);

            String fileName = UUID.randomUUID().toString().concat(".").concat(imageData.format());
            return minioService.uploadFile(bucketName, fileName, imageInputStream, (long) decodedBytes.length, imageData.format());
        } else {
            return null;
        }
    }

}
