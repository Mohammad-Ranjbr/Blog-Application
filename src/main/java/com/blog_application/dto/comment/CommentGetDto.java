package com.blog_application.dto.comment;

import com.blog_application.dto.user.UserBasicInfoDto;
import com.blog_application.dto.user.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class CommentGetDto {

    private Long id;
    private String content;
    private LocalDateTime creationDate;
    private LocalDateTime updatedDate;
    private UserBasicInfoDto user;

}
