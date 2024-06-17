package com.blog_application.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PostUpdateDto {

    private String title;
    private String content;
    private String imageName;

}
