package com.blog_application.dto.post;


import com.blog_application.dto.comment.CommentDto;
import com.blog_application.dto.user.UserDto;
import com.blog_application.dto.category.CategoryDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
    private CategoryDto categoryDto;
    private UserDto userDto;
    private List<CommentDto> commentDtos;

}
