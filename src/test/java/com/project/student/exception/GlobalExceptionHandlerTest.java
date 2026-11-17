package com.project.student.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/students/42");
        webRequest = new ServletWebRequest(request);
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException with 404 and RESOURCE_NOT_FOUND code")
    void handleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Student", "id", 42L);

        ResponseEntity<ErrorDetails> response = exceptionHandler.handleResourceNotFoundException(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getErrorCode()).isEqualTo("RESOURCE_NOT_FOUND");
        assertThat(response.getBody().getMessage()).isEqualTo("Student with id 42 does not exist");
    }

    @Test
    @DisplayName("Should handle BusinessRuleViolationException with 400 and BUSINESS_RULE_VIOLATION code")
    void handleBusinessRuleViolationException() {
        BusinessRuleViolationException ex = new BusinessRuleViolationException("Student with email 'test@mail.com' already exists");

        ResponseEntity<ErrorDetails> response = exceptionHandler.handleBusinessRuleViolationException(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getErrorCode()).isEqualTo("BUSINESS_RULE_VIOLATION");
        assertThat(response.getBody().getMessage()).contains("already exists");
    }

    @Test
    @DisplayName("Should handle DataIntegrityViolationException with 409 and DATA_INTEGRITY_VIOLATION code")
    void handleDataIntegrityViolationException() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Unique constraint violation");

        ResponseEntity<ErrorDetails> response = exceptionHandler.handleDataIntegrityViolationException(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getErrorCode()).isEqualTo("DATA_INTEGRITY_VIOLATION");
        assertThat(response.getBody().getMessage()).isEqualTo("Database constraint violation occurred");
    }

    @Test
    @DisplayName("Should handle Generic Exception with 500 and INTERNAL_SERVER_ERROR code")
    void handleGenericException() {
        Exception ex = new RuntimeException("Unexpected failure");

        ResponseEntity<ErrorDetails> response = exceptionHandler.handleGenericException(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getErrorCode()).isEqualTo("INTERNAL_SERVER_ERROR");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with 400 and list of field errors")
    void handleMethodArgumentNotValidException() throws Exception {
        Method method = this.getClass().getDeclaredMethod("setUp");
        MethodParameter parameter = new MethodParameter(method, -1);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "studentCreateRequest");
        bindingResult.addError(new FieldError("studentCreateRequest", "email", "Email is required"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorDetails> response = exceptionHandler.handleValidationException(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getErrorCode()).isEqualTo("VALIDATION_FAILED");

        List<String> details = response.getBody().getDetails();
        assertThat(details).contains("email: Email is required");
    }
}
