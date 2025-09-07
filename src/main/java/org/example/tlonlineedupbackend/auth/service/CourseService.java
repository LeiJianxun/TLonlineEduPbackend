package org.example.tlonlineedupbackend.auth.service;

import org.example.tlonlineedupbackend.auth.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CourseService {
    Course addCourse(Course course);
    void deleteCourse(Long id);
    Optional<Course> getCourse(Long id);
    Page<Course> getAllCourses(Pageable pageable);
    List<Course> getAscriptionCourses(Long ascription);

    void assignCourseToDepartment(Long courseId, Long departmentId);

    List<Course> getCoursesByDepartment(Long departmentId);

    List<Map<String, Object>> getHotCourses();

    void enterCourse(Long courseId);

    void leaveCourse(Long courseId);

    List<Course> searchCourse(String keyword);
}
