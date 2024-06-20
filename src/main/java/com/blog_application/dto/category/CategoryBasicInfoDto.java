package com.blog_application.dto.category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class CategoryBasicInfoDto {

    private Long id;
    private String title;

}
