package org.example.tlonlineedupbackend.auth.repository;

import org.example.tlonlineedupbackend.auth.entity.CourseTask;
import org.example.tlonlineedupbackend.auth.entity.StudentTask;
import org.example.tlonlineedupbackend.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentTaskRepository extends JpaRepository<StudentTask, Long> {
    List<StudentTask> findByTask(CourseTask task);
    List<StudentTask> findByStudentId(String studentId);
    List<StudentTask> findByStudentAndTask(User student, CourseTask task);
}
