package com.blog_application.dto.tag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class TagBasicInfoDto {

    private Long id;
    private String name;
    private String description;

}
