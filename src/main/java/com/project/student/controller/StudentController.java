package com.project.student.controller;

import com.project.student.dto.request.StudentCreateRequest;
import com.project.student.dto.request.StudentUpdateRequest;
import com.project.student.dto.response.PageResponse;
import com.project.student.dto.response.StudentResponse;
import com.project.student.service.StudentService;
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

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Student Controller", description = "Student CRUD APIs")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @Operation(summary = "Create Student")
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentCreateRequest request) {
        StudentResponse response = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Student by ID")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        StudentResponse response = studentService.getStudentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all students paginated")
    public ResponseEntity<PageResponse<StudentResponse>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PageResponse<StudentResponse> pageResponse = studentService.getAllStudents(page, size, sortBy, sortDir);
        return ResponseEntity.ok(pageResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update student")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentUpdateRequest request) {
        StudentResponse response = studentService.updateStudent(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete student")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search students")
    public ResponseEntity<PageResponse<StudentResponse>> searchStudents(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PageResponse<StudentResponse> response = studentService.searchStudents(keyword, page, size, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }
}
