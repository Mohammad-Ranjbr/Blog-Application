package com.blog_application.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserUpdateDto {

    private String name;
    private String email;
    private String about;
    private String gender;
    private String userName;
    private String phoneNumber;

}
