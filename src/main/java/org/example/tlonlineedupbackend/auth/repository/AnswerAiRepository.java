package org.example.tlonlineedupbackend.auth.repository;

import org.example.tlonlineedupbackend.auth.entity.AnswerAI;
import org.example.tlonlineedupbackend.auth.entity.QuestionAI;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerAiRepository extends JpaRepository<AnswerAI, Long> {
    // 根据问题ID查找答案
    List<AnswerAI> findByQuestionId(Long questionId);

    // 根据学生ID查找答案
//    List<AnswerAI> findByStudentId(Long studentId);

    // 根据问题ID和学生ID查找答案
//    AnswerAI findByQuestionIdAndStudentId(Long questionId, Long studentId);
}
