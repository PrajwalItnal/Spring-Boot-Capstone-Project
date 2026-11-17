package com.project.student.service.impl;

import com.project.student.dto.request.StudentCreateRequest;
import com.project.student.dto.request.StudentUpdateRequest;
import com.project.student.dto.response.PageResponse;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;

    @Override
    public StudentResponse createStudent(StudentCreateRequest request) {
        log.info("Attempting to create student with email: {} and enrollment: {}",
                request.getEmail(), request.getEnrollmentNumber());

        if (studentRepository.existsByEmail(request.getEmail())) {
            log.warn("Failed to create student: Email '{}' already registered", request.getEmail());
            throw new BusinessRuleViolationException(
                    String.format("Student with email '%s' already exists", request.getEmail()));
        }

        if (studentRepository.existsByEnrollmentNumber(request.getEnrollmentNumber())) {
            log.warn("Failed to create student: Enrollment number '{}' already exists",
                    request.getEnrollmentNumber());
            throw new BusinessRuleViolationException(
                    String.format("Student with enrollment number '%s' already exists",
                            request.getEnrollmentNumber()));
        }

        Student student = Student.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .dateOfBirth(request.getDateOfBirth())
                .department(request.getDepartment().trim())
                .enrollmentNumber(request.getEnrollmentNumber().trim().toUpperCase())
                .isActive(true)
                .build();

        Student savedStudent = studentRepository.save(student);
        log.info("Successfully created student with id: {}", savedStudent.getId());
        return StudentResponse.fromEntity(savedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        log.info("Fetching student with id: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
        return StudentResponse.fromEntity(student);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> getAllStudents(int page, int size, String sortBy, String sortDir) {
        log.info("Fetching all students - page: {}, size: {}, sortBy: {}, sortDir: {}",
                page, size, sortBy, sortDir);

        Pageable pageable = createPageable(page, size, sortBy, sortDir);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return PageResponse.from(studentPage.map(StudentResponse::fromEntity));
    }

    @Override
    public StudentResponse updateStudent(Long id, StudentUpdateRequest request) {
        log.info("Attempting to update student with id: {}", id);

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        if (studentRepository.existsByEmailAndIdNot(request.getEmail().trim().toLowerCase(), id)) {
            log.warn("Failed to update student {}: Email '{}' already taken by another student",
                    id, request.getEmail());
            throw new BusinessRuleViolationException(
                    String.format("Email '%s' is already in use by another student", request.getEmail()));
        }

        if (studentRepository.existsByEnrollmentNumberAndIdNot(
                request.getEnrollmentNumber().trim().toUpperCase(), id)) {
            log.warn("Failed to update student {}: Enrollment '{}' already taken by another student",
                    id, request.getEnrollmentNumber());
            throw new BusinessRuleViolationException(
                    String.format("Enrollment number '%s' is already in use by another student",
                            request.getEnrollmentNumber()));
        }

        student.setFirstName(request.getFirstName().trim());
        student.setLastName(request.getLastName().trim());
        student.setEmail(request.getEmail().trim().toLowerCase());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setDepartment(request.getDepartment().trim());
        student.setEnrollmentNumber(request.getEnrollmentNumber().trim().toUpperCase());
        student.setIsActive(request.getIsActive());

        Student updatedStudent = studentRepository.save(student);
        log.info("Successfully updated student with id: {}", updatedStudent.getId());
        return StudentResponse.fromEntity(updatedStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        log.info("Attempting to delete student with id: {}", id);

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        // Performing soft delete to preserve audit history
        student.setIsActive(false);
        studentRepository.save(student);
        log.info("Successfully soft-deleted student with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> searchStudents(
            String keyword,
            String department,
            Boolean isActive,
            int page,
            int size,
            String sortBy,
            String sortDir) {
        log.info("Searching students - keyword: '{}', department: '{}', isActive: {}, page: {}, size: {}",
                keyword, department, isActive, page, size);

        Pageable pageable = createPageable(page, size, sortBy, sortDir);
        Page<Student> studentPage = studentRepository.searchStudents(keyword, department, isActive, pageable);
        return PageResponse.from(studentPage.map(StudentResponse::fromEntity));
    }

    private Pageable createPageable(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name())
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }
}
