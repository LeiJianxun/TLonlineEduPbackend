package org.example.tlonlineedupbackend.auth.service;

import org.example.tlonlineedupbackend.auth.dto.AnswerSubmitDTO;
import org.example.tlonlineedupbackend.auth.entity.AnswerAI;
import org.example.tlonlineedupbackend.auth.entity.QuestionAI;
import org.example.tlonlineedupbackend.auth.entity.AnswerAI;

import java.util.List;
import java.util.Optional;

public interface AnswerAiService {
    // 提交答案
    AnswerAI submitAnswer(AnswerSubmitDTO dto);

    // 根据问题ID获取答案
    List<AnswerAI> getAnswersByQuestionId(Long questionId);

    // 根据学生ID获取答案
//    List<AnswerAI> getAnswersByStudentId(Long studentId);
//
//    // 根据问题ID和学生ID获取答案
//    AnswerAI getAnswerByQuestionIdAndStudentId(Long questionId, Long studentId);
}
