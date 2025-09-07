package org.example.tlonlineedupbackend.auth.repository;

import org.example.tlonlineedupbackend.auth.entity.Course;
import org.example.tlonlineedupbackend.auth.entity.CourseTask;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.List;

public interface CourseTaskRepository extends JpaRepository<CourseTask, Long> {
    List<CourseTask> findByCourse(Course course);
    List<CourseTask> findByDepartmentId(Long departmentId);
}
