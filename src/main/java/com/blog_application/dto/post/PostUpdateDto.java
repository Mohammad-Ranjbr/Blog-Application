package com.blog_application.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PostUpdateDto extends PostCreateDto{

    private String imageName;

}
