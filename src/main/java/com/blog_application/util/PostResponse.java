package com.blog_application.util;

import com.blog_application.dto.post.PostGetDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostResponse {

    private List<PostGetDto> content;
    private int pageSize;
    private int pageNumber;
    private int totalPages;
    private Long totalElements;
    private boolean lastPage;

}
