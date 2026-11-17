package com.project.student.util;

public final class AppConstants {

    private AppConstants() {
        // Private constructor to prevent instantiation
    }

    // Pagination and Sorting defaults
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";

    // Standard Success Messages
    public static final String OPERATION_SUCCESS = "Operation completed successfully";
    public static final String STUDENT_CREATED = "Student created successfully";
    public static final String STUDENT_UPDATED = "Student updated successfully";
    public static final String STUDENT_DELETED = "Student deleted successfully";
    public static final String STUDENT_FETCHED = "Student retrieved successfully";
    public static final String STUDENTS_FETCHED = "Students retrieved successfully";

    // Standard Error Codes
    public static final String ERROR_RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String ERROR_VALIDATION_FAILED = "VALIDATION_FAILED";
    public static final String ERROR_BUSINESS_RULE_VIOLATION = "BUSINESS_RULE_VIOLATION";
    public static final String ERROR_DATA_INTEGRITY_VIOLATION = "DATA_INTEGRITY_VIOLATION";
    public static final String ERROR_INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
}
