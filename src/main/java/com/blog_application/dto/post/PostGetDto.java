package com.blog_application.dto.post;

import com.blog_application.dto.category.CategoryBasicInfoDto;
import com.blog_application.dto.comment.CommentGetDto;
import com.blog_application.dto.user.UserBasicInfoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class PostGetDto {

    private Long id;
    private String title;
    private String content;
    private String imageName;
    private LocalDateTime creationDate;
    private LocalDateTime updatedDate;
    private CategoryBasicInfoDto category;
    private UserBasicInfoDto user;
    private List<CommentGetDto> comments;

}
