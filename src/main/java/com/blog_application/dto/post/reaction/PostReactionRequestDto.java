package com.blog_application.dto.post.reaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class PostReactionRequestDto {

    private UUID userId;
    private Long commentId;
    private boolean isLike;

}
