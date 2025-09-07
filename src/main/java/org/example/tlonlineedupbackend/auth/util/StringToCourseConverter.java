package org.example.tlonlineedupbackend.auth.util;

import org.example.tlonlineedupbackend.auth.entity.Course;
import org.example.tlonlineedupbackend.auth.repository.CourseRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToCourseConverter implements Converter<String, Course> {
    private final CourseRepository courseRepository;

    // 通过构造器注入依赖
    public StringToCourseConverter(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public Course convert(String source) {
        Long courseId = Long.parseLong(source);
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("无效的课程ID: " + courseId));
    }
}
