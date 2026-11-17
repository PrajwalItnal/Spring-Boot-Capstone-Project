package com.project.student.service;

import com.project.student.dto.request.StudentCreateRequest;
import com.project.student.dto.request.StudentUpdateRequest;
import com.project.student.dto.response.StudentResponse;

import java.util.List;

public interface StudentService {

    StudentResponse createStudent(StudentCreateRequest request);

    StudentResponse getStudentById(Long id);

    List<StudentResponse> getAllStudents(int page, int size, String sortBy, String sortDir);

    StudentResponse updateStudent(Long id, StudentUpdateRequest request);

    void deleteStudent(Long id);

    List<StudentResponse> searchStudents(String keyword, int page, int size, String sortBy, String sortDir);
}
