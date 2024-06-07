package com.blog_application.util;

import com.blog_application.dto.PostDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostResponse {

    private List<PostDto> content;
    private int pageSize;
    private int pageNumber;
    private int totalPages;
    private Long totalElements;
    private boolean lastPage;

}
