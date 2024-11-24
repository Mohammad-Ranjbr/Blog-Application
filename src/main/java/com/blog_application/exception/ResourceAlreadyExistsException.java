package com.blog_application.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceAlreadyExistsException extends RuntimeException{

    private String resourceName;
    private String fieldName;
    private String fieldValue;
    private String action;

    public ResourceAlreadyExistsException(String resourceName, String fieldName, String fieldValue,String action) {
        super(String.format("%s already exists with %s : %s",resourceName,fieldName,fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.action = action;
    }

}
