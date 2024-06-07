package com.blog_application.dto;

import com.blog_application.model.Category;
import com.blog_application.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PostDto {

    private Long id;
    private String title;
    private String content;
    private String imageName;
    private LocalDateTime creationDate;
    private LocalDateTime updatedDate;
    private Category category;
    private User user;

}
