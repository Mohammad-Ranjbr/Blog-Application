package com.blog_application.dto.user;

import com.blog_application.dto.post.PostGetDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class UserGetDto extends UserBasicInfoDto{

    private String about;
    private String gender;
    private String userName;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    //private List<PostGetDto> posts;

}
