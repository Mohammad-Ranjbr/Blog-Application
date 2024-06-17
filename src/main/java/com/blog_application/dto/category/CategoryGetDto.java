package com.blog_application.dto.category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class CategoryGetDto extends CategoryBasicInfoDto{

    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
