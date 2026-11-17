package com.project.student.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {
    private boolean success;
    private String errorCode;
    private String message;
    private List<String> details;
    private Instant timestamp;
}
