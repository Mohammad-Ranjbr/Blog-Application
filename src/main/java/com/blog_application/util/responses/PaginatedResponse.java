package com.blog_application.util.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponse<T> {

    private List<T> content;
    private int pageSize;
    private int pageNumber;
    private int totalPages;
    private Long totalElements;
    private boolean lastPage;

}
