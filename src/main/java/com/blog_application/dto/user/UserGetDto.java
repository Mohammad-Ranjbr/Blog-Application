package com.blog_application.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class UserGetDto extends UserBasicInfoDto{

    private String about;
    private String userName;
    private String phoneNumber;
    private int followersCount;
    private int followingCount;
    private int postsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive;
    private boolean softDelete;
    private String imageName;
    private String image;
    private boolean isFollowedByCurrentUser;

}
