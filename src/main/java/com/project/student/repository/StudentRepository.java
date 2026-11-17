package com.project.student.repository;

import com.project.student.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);

    boolean existsByEnrollmentNumber(String enrollmentNumber);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByEnrollmentNumberAndIdNot(String enrollmentNumber, Long id);

    Page<Student> findByIsActive(Boolean isActive, Pageable pageable);

    @Query("SELECT s FROM Student s WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.department) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.enrollmentNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:department IS NULL OR :department = '' OR LOWER(s.department) = LOWER(:department)) AND " +
            "(:isActive IS NULL OR s.isActive = :isActive)")
    Page<Student> searchStudents(
            @Param("keyword") String keyword,
            @Param("department") String department,
            @Param("isActive") Boolean isActive,
            Pageable pageable);
}
