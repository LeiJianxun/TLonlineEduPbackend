package org.example.tlonlineedupbackend.auth.repository;

import org.example.tlonlineedupbackend.auth.entity.Course;
import org.example.tlonlineedupbackend.auth.entity.CourseDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseDepartmentRepository extends JpaRepository<CourseDepartment, Long> {
    List<CourseDepartment> findByDepartmentId(Long departmentId);
    boolean existsByCourseIdAndDepartmentId(Long courseId, Long departmentId);
    List<CourseDepartment> findByCourse(Course course);
}
