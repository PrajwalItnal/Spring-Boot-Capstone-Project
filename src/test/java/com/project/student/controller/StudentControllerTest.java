package com.project.student.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.student.config.SecurityConfig;
import com.project.student.dto.request.StudentCreateRequest;
import com.project.student.dto.request.StudentUpdateRequest;
import com.project.student.dto.response.StudentResponse;
import com.project.student.exception.ResourceNotFoundException;
import com.project.student.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StudentController.class)
@Import(SecurityConfig.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private StudentResponse studentResponse;
    private StudentCreateRequest createRequest;
    private StudentUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        studentResponse = StudentResponse.builder()
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
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /api/v1/students - Should return 201 Created on valid payload")
    void createStudent_Success() throws Exception {
        when(studentService.createStudent(any(StudentCreateRequest.class))).thenReturn(studentResponse);

        mockMvc.perform(post("/api/v1/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("john.doe@university.edu")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /api/v1/students - Should return 400 Bad Request on invalid input")
    void createStudent_ValidationError() throws Exception {
        StudentCreateRequest invalidRequest = StudentCreateRequest.builder()
                .firstName("")
                .email("invalid-email")
                .build();

        mockMvc.perform(post("/api/v1/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("VALIDATION_FAILED")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/students/{id} - Should return 200 OK when found")
    void getStudentById_Success() throws Exception {
        when(studentService.getStudentById(1L)).thenReturn(studentResponse);

        mockMvc.perform(get("/api/v1/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/students/{id} - Should return 404 when student not found")
    void getStudentById_NotFound() throws Exception {
        when(studentService.getStudentById(42L))
                .thenThrow(new ResourceNotFoundException("Student", "id", 42L));

        mockMvc.perform(get("/api/v1/students/42"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("RESOURCE_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Student with id 42 does not exist")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/students - Should return 200 OK with list of students")
    void getAllStudents_Success() throws Exception {
        List<StudentResponse> list = Collections.singletonList(studentResponse);

        when(studentService.getAllStudents(anyInt(), anyInt(), anyString(), anyString())).thenReturn(list);

        mockMvc.perform(get("/api/v1/students?page=0&size=10&sortBy=id&sortDir=asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PUT /api/v1/students/{id} - Should return 200 OK on full update")
    void updateStudent_Success() throws Exception {
        when(studentService.updateStudent(eq(1L), any(StudentUpdateRequest.class))).thenReturn(studentResponse);

        mockMvc.perform(put("/api/v1/students/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("DELETE /api/v1/students/{id} - Should return 204 No Content on delete")
    void deleteStudent_Success() throws Exception {
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/api/v1/students/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/students/search - Should return 200 OK on query filter")
    void searchStudents_Success() throws Exception {
        List<StudentResponse> list = Collections.singletonList(studentResponse);

        when(studentService.searchStudents(any(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(list);

        mockMvc.perform(get("/api/v1/students/search?keyword=John&page=0&size=10&sortBy=id&sortDir=asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName", is("John")));
    }

    @Test
    @DisplayName("GET /api/v1/students - Should return 401 Unauthorized when unauthenticated")
    void unauthenticatedAccess_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/students?page=0&size=10&sortBy=id&sortDir=asc"))
                .andExpect(status().isUnauthorized());
    }
}
