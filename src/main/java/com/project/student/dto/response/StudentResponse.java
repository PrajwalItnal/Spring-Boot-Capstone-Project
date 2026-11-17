package com.project.student.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.student.entity.Student;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object representing a student")
public class StudentResponse {

    @Schema(description = "Unique database identifier", example = "1")
    private Long id;

    @Schema(description = "First name", example = "John")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Schema(description = "Email address", example = "john.doe@university.edu")
    private String email;

    @Schema(description = "Date of birth", example = "2002-05-15")
    private LocalDate dateOfBirth;

    @Schema(description = "Department", example = "Computer Science")
    private String department;

    @Schema(description = "Enrollment number", example = "CS-2026-001")
    private String enrollmentNumber;

    @Schema(description = "Active status", example = "true")
    private Boolean isActive;

    @Schema(description = "Record creation timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Record last modification timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(description = "Created by user", example = "admin")
    private String createdBy;

    @Schema(description = "Updated by user", example = "admin")
    private String updatedBy;

    public static StudentResponse fromEntity(Student student) {
        if (student == null) {
            return null;
        }
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
                .createdBy(student.getCreatedBy())
                .updatedBy(student.getUpdatedBy())
                .build();
    }
}
