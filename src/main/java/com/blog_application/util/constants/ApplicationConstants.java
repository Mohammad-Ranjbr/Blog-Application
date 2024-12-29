package com.blog_application.util.constants;

public class ApplicationConstants {

    public static final String PAGE_NUMBER = "pageNumber";
    public static final String DEFAULT_PAGE_NUMBER = "0";

    public static final String PAGE_SIZE = "pageSize";
    public static final String DEFAULT_PAGE_SIZE = "25";

    public static final String SORT_BY = "sortBy";
    public static final String DEFAULT_POST_SORT_BY = "title";
    public static final String DEFAULT_USER_SORT_BY = "name";
    public static final String DEFAULT_CATEGORY_SORT_BY = "title";
    public static final String DEFAULT_TAG_SORT_BY = "name";

    public static final String SORT_DIR = "sortDir";
    public static final String DEFAULT_SORT_DIR = "asc";

    public static final String PHONE_PATTERN_REGEX = "^09\\d{9}$";
    public static final String PASSWORD_PATTERN_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,}$";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String JWT_SECRET_KEY = "JWT_SECRET";
    public static final String JWT_SECRET_DEFAULT_VALUE = "jxgEQeXHuPq8VdbyYFNkANdudQ53yUn4";
    public static final String JWT_HEADER = "Authorization";

}
