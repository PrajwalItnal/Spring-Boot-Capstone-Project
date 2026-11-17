package com.project.student.exception;

import com.project.student.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("Validation error on request [{}]: {}", extractPath(request), ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorDetails errorDetails = ErrorDetails.builder()
                .success(false)
                .errorCode(AppConstants.ERROR_VALIDATION_FAILED)
                .message("Validation failed for input parameters")
                .details(fieldErrors)
                .timestamp(Instant.now())
                .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());

        ErrorDetails errorDetails = ErrorDetails.builder()
                .success(false)
                .errorCode(AppConstants.ERROR_RESOURCE_NOT_FOUND)
                .message(ex.getMessage())
                .details(Collections.singletonList("Path: " + extractPath(request)))
                .timestamp(Instant.now())
                .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorDetails> handleBusinessRuleViolationException(
            BusinessRuleViolationException ex, WebRequest request) {
        log.warn("Business rule violation: {}", ex.getMessage());

        ErrorDetails errorDetails = ErrorDetails.builder()
                .success(false)
                .errorCode(AppConstants.ERROR_BUSINESS_RULE_VIOLATION)
                .message(ex.getMessage())
                .details(Collections.singletonList("Path: " + extractPath(request)))
                .timestamp(Instant.now())
                .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDetails> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {
        log.error("Data integrity violation: {}", ex.getMessage(), ex);

        String rootCauseMessage = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();

        ErrorDetails errorDetails = ErrorDetails.builder()
                .success(false)
                .errorCode(AppConstants.ERROR_DATA_INTEGRITY_VIOLATION)
                .message("Database constraint violation occurred")
                .details(List.of("Path: " + extractPath(request), "Cause: " + rootCauseMessage))
                .timestamp(Instant.now())
                .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGenericException(
            Exception ex, WebRequest request) {
        log.error("Unhandled internal exception occurred at [{}]: {}", extractPath(request), ex.getMessage(), ex);

        ErrorDetails errorDetails = ErrorDetails.builder()
                .success(false)
                .errorCode(AppConstants.ERROR_INTERNAL_SERVER_ERROR)
                .message("An unexpected error occurred. Please contact system administrator.")
                .details(Collections.singletonList("Path: " + extractPath(request)))
                .timestamp(Instant.now())
                .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
