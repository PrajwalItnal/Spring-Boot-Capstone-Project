package com.project.student.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + ": " + error.getDefaultMessage());
        }

        ErrorDetails error = ErrorDetails.builder()
                .success(false)
                .errorCode("VALIDATION_FAILED")
                .message("Validation failed for input parameters")
                .details(details)
                .timestamp(Instant.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorDetails error = ErrorDetails.builder()
                .success(false)
                .errorCode("RESOURCE_NOT_FOUND")
                .message(ex.getMessage())
                .details(List.of("Path: " + request.getDescription(false).replace("uri=", "")))
                .timestamp(Instant.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorDetails> handleBusinessRuleViolationException(BusinessRuleViolationException ex, WebRequest request) {
        ErrorDetails error = ErrorDetails.builder()
                .success(false)
                .errorCode("BUSINESS_RULE_VIOLATION")
                .message(ex.getMessage())
                .details(List.of("Path: " + request.getDescription(false).replace("uri=", "")))
                .timestamp(Instant.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDetails> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        ErrorDetails error = ErrorDetails.builder()
                .success(false)
                .errorCode("DATA_INTEGRITY_VIOLATION")
                .message("Database constraint violation occurred")
                .details(List.of("Path: " + request.getDescription(false).replace("uri=", "")))
                .timestamp(Instant.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGenericException(Exception ex, WebRequest request) {
        ErrorDetails error = ErrorDetails.builder()
                .success(false)
                .errorCode("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred. Please contact system administrator.")
                .details(List.of("Path: " + request.getDescription(false).replace("uri=", "")))
                .timestamp(Instant.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
