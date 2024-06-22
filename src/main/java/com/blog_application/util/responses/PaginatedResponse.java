package com.blog_application.util.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PaginatedResponse<T> {

    private List<T> content;
    private int pageSize;
    private int pageNumber;
    private int totalPages;
    private Long totalElements;
    private boolean lastPage;

}
