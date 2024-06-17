package com.blog_application.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserBasicInfoDto {

    private Long id;
    private String name;
    private String email;

}
