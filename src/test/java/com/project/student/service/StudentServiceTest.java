package com.project.student.service;

import com.project.student.dto.request.StudentCreateRequest;
import com.project.student.dto.request.StudentUpdateRequest;
import com.project.student.dto.response.StudentResponse;
import com.project.student.entity.Student;
import com.project.student.exception.BusinessRuleViolationException;
import com.project.student.exception.ResourceNotFoundException;
import com.project.student.repository.StudentRepository;
import com.project.student.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student student;
    private StudentCreateRequest createRequest;
    private StudentUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        student = Student.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@university.edu")
                .dateOfBirth(LocalDate.of(2002, 5, 15))
                .department("Computer Science")
                .enrollmentNumber("CS-2026-001")
                .isActive(true)
                .build();

        createRequest = StudentCreateRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@university.edu")
                .dateOfBirth(LocalDate.of(2002, 5, 15))
                .department("Computer Science")
                .enrollmentNumber("CS-2026-001")
                .build();

        updateRequest = StudentUpdateRequest.builder()
                .firstName("Johnathan")
                .lastName("Doe")
                .email("johnathan.doe@university.edu")
                .dateOfBirth(LocalDate.of(2002, 5, 15))
                .department("Software Engineering")
                .enrollmentNumber("CS-2026-001")
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Should successfully create a student when inputs are valid")
    void createStudent_Success() {
        when(studentRepository.existsByEmail(anyString())).thenReturn(false);
        when(studentRepository.existsByEnrollmentNumber(anyString())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentResponse response = studentService.createStudent(createRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("john.doe@university.edu");
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw BusinessRuleViolationException when creating student with duplicate email")
    void createStudent_ThrowsBusinessRuleViolation_WhenEmailExists() {
        when(studentRepository.existsByEmail(createRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> studentService.createStudent(createRequest))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("already exists");

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw BusinessRuleViolationException when creating student with duplicate enrollment number")
    void createStudent_ThrowsBusinessRuleViolation_WhenEnrollmentExists() {
        when(studentRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(studentRepository.existsByEnrollmentNumber(createRequest.getEnrollmentNumber())).thenReturn(true);

        assertThatThrownBy(() -> studentService.createStudent(createRequest))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("enrollment number");

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should return student response when student exists by ID")
    void getStudentById_Success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        StudentResponse response = studentService.getStudentById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when student does not exist by ID")
    void getStudentById_NotFound() {
        when(studentRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudentById(42L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Student with id 42 does not exist");
    }

    @Test
    @DisplayName("Should return students list")
    void getAllStudents_Success() {
        Page<Student> page = new PageImpl<>(Collections.singletonList(student));
        when(studentRepository.findAll(any(Pageable.class))).thenReturn(page);

        List<StudentResponse> response = studentService.getAllStudents(0, 10, "id", "asc");

        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should update student successfully")
    void updateStudent_Success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.existsByEmailAndIdNot(anyString(), eq(1L))).thenReturn(false);
        when(studentRepository.existsByEnrollmentNumberAndIdNot(anyString(), eq(1L))).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentResponse response = studentService.updateStudent(1L, updateRequest);

        assertThat(response).isNotNull();
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent student")
    void updateStudent_NotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.updateStudent(99L, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Student with id 99 does not exist");
    }

    @Test
    @DisplayName("Should soft delete student by ID")
    void deleteStudent_Success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        studentService.deleteStudent(1L);

        assertThat(student.getIsActive()).isFalse();
        verify(studentRepository).save(student);
    }

    @Test
    @DisplayName("Should search students matching query")
    void searchStudents_Success() {
        Page<Student> page = new PageImpl<>(Collections.singletonList(student));
        when(studentRepository.findByFirstNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
                eq("John"), eq("John"), any(Pageable.class))).thenReturn(page);

        List<StudentResponse> response = studentService.searchStudents("John", 0, 10, "id", "asc");

        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getFirstName()).isEqualTo("John");
    }
}
