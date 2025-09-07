package org.example.tlonlineedupbackend.auth.repository;

import org.example.tlonlineedupbackend.auth.entity.ExerciseAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface exerciseAnswerRepository extends JpaRepository<ExerciseAnswer, Long> {
    Optional<ExerciseAnswer> findById(Long exerciseId);
}
