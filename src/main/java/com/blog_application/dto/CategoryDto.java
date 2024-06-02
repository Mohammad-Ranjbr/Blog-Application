package com.blog_application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDto {

    private Long id;
    @NotBlank
    @Size(min = 4)
    private String title;
    @NotBlank
    @Size(min = 10)
    private String description;

}
