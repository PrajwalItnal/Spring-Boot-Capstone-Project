package com.project.student.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard error response envelope")
public class ErrorDetails {

    @Builder.Default
    @Schema(description = "Success flag, always false for errors", example = "false")
    private boolean success = false;

    @Schema(description = "Machine-readable error code", example = "RESOURCE_NOT_FOUND")
    private String errorCode;

    @Schema(description = "Human-readable error description", example = "Student with id 42 does not exist")
    private String message;

    @Schema(description = "Contextual error details or field validation issues")
    private Object details;

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @Schema(description = "Timestamp when error occurred", example = "2026-11-17T20:58:30.123Z")
    private Instant timestamp = Instant.now();
}
