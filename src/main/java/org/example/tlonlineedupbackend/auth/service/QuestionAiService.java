package org.example.tlonlineedupbackend.auth.service;

import org.example.tlonlineedupbackend.auth.dto.QuestionCreateDTO;
import org.example.tlonlineedupbackend.auth.entity.Course;
import org.example.tlonlineedupbackend.auth.entity.QuestionAI;

import java.util.List;
import java.util.Optional;

public interface QuestionAiService {
    // 创建问题
    QuestionAI createQuestion(QuestionCreateDTO dto);

    // 根据ID获取问题
    QuestionAI getQuestionById(Long id);

    // 根据课程ID获取问题列表
    List<QuestionAI> getQuestionsByCourseId(Long courseId);

    // 根据学生ID获取问题列表
    List<QuestionAI> getQuestionsByStudentId(String studentId);

    // 更新问题状态
    QuestionAI updateQuestionStatus(Long questionId, Integer status);

    Object getQuestionContent(Long questionId);
}
