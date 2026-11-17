package com.project.student.service.impl;

import com.project.student.dto.request.StudentCreateRequest;
import com.project.student.dto.request.StudentUpdateRequest;
import com.project.student.dto.response.StudentResponse;
import com.project.student.entity.Student;
import com.project.student.exception.BusinessRuleViolationException;
import com.project.student.exception.ResourceNotFoundException;
import com.project.student.repository.StudentRepository;
import com.project.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);
    private final StudentRepository studentRepository;

    @Override
    public StudentResponse createStudent(StudentCreateRequest request) {
        log.info("Creating student with email: {}", request.getEmail());

        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleViolationException(
                    "Student with email '" + request.getEmail() + "' already exists");
        }

        if (studentRepository.existsByEnrollmentNumber(request.getEnrollmentNumber())) {
            throw new BusinessRuleViolationException(
                    "Student with enrollment number '" + request.getEnrollmentNumber() + "' already exists");
        }

        Student student = Student.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .department(request.getDepartment())
                .enrollmentNumber(request.getEnrollmentNumber())
                .isActive(true)
                .build();

        Student saved = studentRepository.save(student);
        return mapToResponse(saved);
    }

    @Override
    public StudentResponse getStudentById(Long id) {
        log.info("Getting student by id: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
        return mapToResponse(student);
    }

    @Override
    public List<StudentResponse> getAllStudents(int page, int size, String sortBy, String sortDir) {
        log.info("Getting all students - page: {}, size: {}", page, size);
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Student> studentPage = studentRepository.findAll(pageable);

        List<StudentResponse> list = new ArrayList<>();
        for (Student s : studentPage.getContent()) {
            list.add(mapToResponse(s));
        }
        return list;
    }

    @Override
    public StudentResponse updateStudent(Long id, StudentUpdateRequest request) {
        log.info("Updating student id: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        if (studentRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new BusinessRuleViolationException("Email is already used by another student");
        }

        if (studentRepository.existsByEnrollmentNumberAndIdNot(request.getEnrollmentNumber(), id)) {
            throw new BusinessRuleViolationException("Enrollment number is already used by another student");
        }

        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setDepartment(request.getDepartment());
        student.setEnrollmentNumber(request.getEnrollmentNumber());
        if (request.getIsActive() != null) {
            student.setIsActive(request.getIsActive());
        }

        Student updated = studentRepository.save(student);
        return mapToResponse(updated);
    }

    @Override
    public void deleteStudent(Long id) {
        log.info("Deleting student id: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        student.setIsActive(false);
        studentRepository.save(student);
    }

    @Override
    public List<StudentResponse> searchStudents(String keyword, int page, int size, String sortBy, String sortDir) {
        log.info("Searching students with keyword: {}", keyword);
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        String search = (keyword == null) ? "" : keyword;
        Page<Student> studentPage = studentRepository.findByFirstNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(search, search, pageable);

        List<StudentResponse> list = new ArrayList<>();
        for (Student s : studentPage.getContent()) {
            list.add(mapToResponse(s));
        }
        return list;
    }

    private StudentResponse mapToResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .dateOfBirth(student.getDateOfBirth())
                .department(student.getDepartment())
                .enrollmentNumber(student.getEnrollmentNumber())
                .isActive(student.getIsActive())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
    }
}
