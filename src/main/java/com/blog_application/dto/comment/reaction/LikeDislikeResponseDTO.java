package com.blog_application.dto.comment.reaction;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LikeDislikeResponseDTO {

    private Long id;
    private UUID userId;
    private Long commentId;
    private boolean isLike;

}
