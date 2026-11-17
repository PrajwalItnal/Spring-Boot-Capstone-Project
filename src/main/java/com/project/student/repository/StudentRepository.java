package com.project.student.repository;

import com.project.student.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);

    boolean existsByEnrollmentNumber(String enrollmentNumber);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByEnrollmentNumberAndIdNot(String enrollmentNumber, Long id);

    Page<Student> findByFirstNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
            String firstName, String department, Pageable pageable);
}
