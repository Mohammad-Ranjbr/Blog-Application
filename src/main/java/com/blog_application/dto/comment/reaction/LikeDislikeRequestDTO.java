package com.blog_application.dto.comment.reaction;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class LikeDislikeRequestDTO {

    private UUID userId;
    private Long commentId;
    private boolean isLike;

}
