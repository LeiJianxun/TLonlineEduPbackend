package org.example.tlonlineedupbackend.auth.repository;

import org.example.tlonlineedupbackend.auth.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findById(Long id);
    List<Course> findByAscription(Long ascription);
    List<Course> findByNameContaining(String name1);
    List<Course> findByB1Containing(String b1);
    List<Course> findByB2Containing(String b2);
}
