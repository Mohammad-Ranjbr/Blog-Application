package com.blog_application.service.image;

import com.blog_application.dto.image.ImageData;

import java.io.IOException;

public interface ImageService {

    String uploadImage(ImageData imageData) throws IOException;

}
