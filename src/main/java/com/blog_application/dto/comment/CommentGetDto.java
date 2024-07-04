package com.blog_application.dto.comment;

import com.blog_application.dto.user.UserBasicInfoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class CommentGetDto {

    private Long id;
    private String content;
    private LocalDateTime creationDate;
    private LocalDateTime updatedDate;
    private UserBasicInfoDto user;
    private Long parentId;
    private int likes;
    private int dislikes;
    private List<CommentGetDto> replies;

}
