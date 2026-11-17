package com.project.student.controller;

import com.project.student.dto.request.StudentCreateRequest;
import com.project.student.dto.request.StudentUpdateRequest;
import com.project.student.dto.response.ApiResponse;
import com.project.student.dto.response.PageResponse;
import com.project.student.dto.response.StudentResponse;
import com.project.student.service.StudentService;
import com.project.student.util.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Student Controller", description = "Student CRUD APIs")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @Operation(summary = "Create Student")
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(@Valid @RequestBody StudentCreateRequest request) {
        StudentResponse response = studentService.createStudent(request);
        ApiResponse<StudentResponse> apiResponse = ApiResponse.<StudentResponse>builder()
                .success(true)
                .message("Student created successfully")
                .data(response)
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Student by ID")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(@PathVariable Long id) {
        StudentResponse response = studentService.getStudentById(id);
        ApiResponse<StudentResponse> apiResponse = ApiResponse.<StudentResponse>builder()
                .success(true)
                .message("Operation completed successfully")
                .data(response)
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    @Operation(summary = "Get all students paginated")
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> getAllStudents(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {
        PageResponse<StudentResponse> pageResponse = studentService.getAllStudents(page, size, sortBy, sortDir);
        ApiResponse<PageResponse<StudentResponse>> apiResponse = ApiResponse.<PageResponse<StudentResponse>>builder()
                .success(true)
                .message("Operation completed successfully")
                .data(pageResponse)
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update student")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentUpdateRequest request) {
        StudentResponse response = studentService.updateStudent(id, request);
        ApiResponse<StudentResponse> apiResponse = ApiResponse.<StudentResponse>builder()
                .success(true)
                .message("Student updated successfully")
                .data(response)
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete student")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(true)
                .message("Student deleted successfully")
                .data(null)
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/search")
    @Operation(summary = "Search students")
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> searchStudents(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {
        PageResponse<StudentResponse> response = studentService.searchStudents(keyword, page, size, sortBy, sortDir);
        ApiResponse<PageResponse<StudentResponse>> apiResponse = ApiResponse.<PageResponse<StudentResponse>>builder()
                .success(true)
                .message("Operation completed successfully")
                .data(response)
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
