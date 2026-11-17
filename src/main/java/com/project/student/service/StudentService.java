package com.project.student.service;

import com.project.student.dto.request.StudentCreateRequest;
import com.project.student.dto.request.StudentUpdateRequest;
import com.project.student.dto.response.PageResponse;
import com.project.student.dto.response.StudentResponse;

public interface StudentService {

    StudentResponse createStudent(StudentCreateRequest request);

    StudentResponse getStudentById(Long id);

    PageResponse<StudentResponse> getAllStudents(int page, int size, String sortBy, String sortDir);

    StudentResponse updateStudent(Long id, StudentUpdateRequest request);

    void deleteStudent(Long id);

    PageResponse<StudentResponse> searchStudents(
            String keyword,
            String department,
            Boolean isActive,
            int page,
            int size,
            String sortBy,
            String sortDir);
}
