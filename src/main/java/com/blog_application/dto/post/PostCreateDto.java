package com.blog_application.dto.post;

import com.blog_application.dto.image.ImageData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class PostCreateDto {

    @NotBlank(message = "Title is mandatory")
    @Size(max = 100, message = "Title must be less than 100 characters")
    private String title;

    @NotBlank(message = "Content is mandatory")
    @Size(max = 1000, message = "Content must be less than 1000 characters")
    private String content;

    private LocalDateTime scheduledTime;
    private ImageData imageData;

}
