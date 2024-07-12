package com.blog_application.dto.tag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class TagGetDto extends TagBasicInfoDto{

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

}
