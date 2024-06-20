package com.blog_application.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class UserBasicInfoDto {

    private UUID id;
    private String name;
    private String email;

}
