-- PostgreSQL Database Schema for Student Management System
-- Compatible with PostgreSQL 14+

-- Drop table if exists
DROP TABLE IF EXISTS students;

-- Create students table
CREATE TABLE students (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    date_of_birth DATE NOT NULL,
    department VARCHAR(100) NOT NULL,
    enrollment_number VARCHAR(50) NOT NULL UNIQUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

-- Performance Indexes
CREATE INDEX idx_students_email ON students(email);
CREATE INDEX idx_students_enrollment_number ON students(enrollment_number);
CREATE INDEX idx_students_department ON students(department);
CREATE INDEX idx_students_is_active ON students(is_active);
CREATE INDEX idx_students_name ON students(first_name, last_name);

-- Table & Column Documentation
COMMENT ON TABLE students IS 'Stores academic student profiles and enrollment records';
COMMENT ON COLUMN students.id IS 'Primary key auto-incrementing student ID';
COMMENT ON COLUMN students.first_name IS 'Student legal first name';
COMMENT ON COLUMN students.last_name IS 'Student legal last name';
COMMENT ON COLUMN students.email IS 'Unique student institutional or personal email address';
COMMENT ON COLUMN students.date_of_birth IS 'Student date of birth';
COMMENT ON COLUMN students.department IS 'Academic department or major faculty';
COMMENT ON COLUMN students.enrollment_number IS 'Unique roll or enrollment identifier code';
COMMENT ON COLUMN students.is_active IS 'Flag indicating active student status (soft-delete flag)';
COMMENT ON COLUMN students.created_at IS 'Audit timestamp indicating record creation';
COMMENT ON COLUMN students.updated_at IS 'Audit timestamp indicating record modification';
COMMENT ON COLUMN students.created_by IS 'Audit username who created the record';
COMMENT ON COLUMN students.updated_by IS 'Audit username who last modified the record';
