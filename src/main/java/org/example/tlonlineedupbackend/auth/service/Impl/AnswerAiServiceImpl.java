package org.example.tlonlineedupbackend.auth.service.Impl;

import jakarta.transaction.Transactional;
import org.example.tlonlineedupbackend.auth.dto.AnswerSubmitDTO;
import org.example.tlonlineedupbackend.auth.entity.AnswerAI;
import org.example.tlonlineedupbackend.auth.entity.QuestionAI;
import org.example.tlonlineedupbackend.auth.repository.AnswerAiRepository;
import org.example.tlonlineedupbackend.auth.repository.QuestionAiRepository;
import org.example.tlonlineedupbackend.auth.service.AIService;
import org.example.tlonlineedupbackend.auth.service.AnswerAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnswerAiServiceImpl implements AnswerAiService {

    @Autowired
    private AnswerAiRepository answerAiRepository;

    @Autowired
    private AnswerAiRepository answerRepository;

    @Autowired
    private QuestionAiRepository questionRepository;

    @Autowired
    private AIService aiService;

    @Override
    @Transactional
    public AnswerAI submitAnswer(AnswerSubmitDTO dto) {
        // 创建答案对象
        AnswerAI answer = new AnswerAI();
        answer.setQuestionId(dto.getQuestionId());
        answer.setContent(dto.getContent());
        answer.setSubmitTime(LocalDateTime.now());

        // 保存答案
        AnswerAI savedAnswer = answerRepository.save(answer);

        // 更新问题状态为已回答
        QuestionAI question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new RuntimeException("问题不存在"));
        question.setStatus(1); // 1 表示已回答
        questionRepository.save(question);

        // 异步调用AI评分
        aiService.evaluateAnswer(savedAnswer);

        return savedAnswer;
    }

    @Override
    public List<AnswerAI> getAnswersByQuestionId(Long questionId) {
        return answerRepository.findByQuestionId(questionId);
    }

//    @Override
//    public List<AnswerAI> getAnswersByStudentId(Long studentId) {
//        return answerRepository.findByStudentId(studentId);
//    }
//
//    @Override
//    public AnswerAI getAnswerByQuestionIdAndStudentId(Long questionId, Long studentId) {
//        return answerRepository.findByQuestionIdAndStudentId(questionId, studentId);
//    }
}
