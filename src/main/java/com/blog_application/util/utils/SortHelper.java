package com.blog_application.util.utils;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class SortHelper {

    public static Sort getSortOrder(String sortBy,String sortDir){
        return (sortDir.equalsIgnoreCase("acc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    }

}
