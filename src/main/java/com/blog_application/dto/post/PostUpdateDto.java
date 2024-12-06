package com.blog_application.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class PostUpdateDto extends PostCreateDto{

    private String imageName;
    private UUID userId;
    private Long categoryId;

}
