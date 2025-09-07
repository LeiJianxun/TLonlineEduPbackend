package org.example.tlonlineedupbackend.auth.repository;

import org.example.tlonlineedupbackend.auth.entity.QuestionAI;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionAiRepository extends JpaRepository<QuestionAI, Long> {

    Optional<QuestionAI> findById(Long id);

    // 根据课程ID查找问题
    List<QuestionAI> findByCourseId(Long courseId);

    // 根据学生ID查找问题
    List<QuestionAI> findByStudentId(String studentId);

    // 根据状态查找问题（0-未回答，1-已回答）
    List<QuestionAI> findByStatus(Integer status);

    // 根据课程ID和状态查找问题
    List<QuestionAI> findByCourseIdAndStatus(Long courseId, Integer status);
}
