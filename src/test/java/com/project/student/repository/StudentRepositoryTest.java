package com.project.student.repository;

import com.project.student.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    private Student student;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();

        student = Student.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice.smith@university.edu")
                .dateOfBirth(LocalDate.of(2001, 3, 20))
                .department("Electrical Engineering")
                .enrollmentNumber("EE-2026-101")
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Should persist and retrieve student by ID")
    void saveAndFindById() {
        Student saved = studentRepository.save(student);

        assertThat(saved.getId()).isNotNull();
        assertThat(studentRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("Should check if email exists")
    void existsByEmail() {
        studentRepository.save(student);

        assertThat(studentRepository.existsByEmail("alice.smith@university.edu")).isTrue();
        assertThat(studentRepository.existsByEmail("nonexistent@university.edu")).isFalse();
    }

    @Test
    @DisplayName("Should check if enrollment number exists")
    void existsByEnrollmentNumber() {
        studentRepository.save(student);

        assertThat(studentRepository.existsByEnrollmentNumber("EE-2026-101")).isTrue();
        assertThat(studentRepository.existsByEnrollmentNumber("UNKNOWN-000")).isFalse();
    }

    @Test
    @DisplayName("Should search students by first name or department")
    void searchStudents() {
        studentRepository.save(student);

        Page<Student> page = studentRepository.findByFirstNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
                "Alice", "Alice", PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getLastName()).isEqualTo("Smith");
    }
}
