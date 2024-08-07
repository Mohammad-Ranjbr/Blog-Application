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
    private String gender;
    private String userName;
    private String phoneNumber;
    private int followersCount;
    private int followingCount;
    private LocalDateTime creationDate;
    private LocalDateTime updatedDate;

}
