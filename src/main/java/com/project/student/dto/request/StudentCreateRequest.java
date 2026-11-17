package com.project.student.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating a new student")
public class StudentCreateRequest {

    @NotBlank(message = "First name must not be blank")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Schema(description = "First name of the student", example = "John")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Schema(description = "Last name of the student", example = "Doe")
    private String lastName;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Schema(description = "Unique email address of the student", example = "john.doe@university.edu")
    private String email;

    @NotNull(message = "Date of birth must not be null")
    @Past(message = "Date of birth must be a date in the past")
    @Schema(description = "Date of birth of the student", example = "2002-05-15")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Department must not be blank")
    @Size(min = 2, max = 100, message = "Department must be between 2 and 100 characters")
    @Schema(description = "Department or major of the student", example = "Computer Science")
    private String department;

    @NotBlank(message = "Enrollment number must not be blank")
    @Size(min = 3, max = 50, message = "Enrollment number must be between 3 and 50 characters")
    @Schema(description = "Unique enrollment or roll number", example = "CS-2026-001")
    private String enrollmentNumber;
}
