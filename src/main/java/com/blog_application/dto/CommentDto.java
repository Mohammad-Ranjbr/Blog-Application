package com.blog_application.dto;

import com.blog_application.model.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentDto {

    private Long id;
    private String content;
    private LocalDateTime creationDate;
    private LocalDateTime updatedDate;
    private Post post;

}
