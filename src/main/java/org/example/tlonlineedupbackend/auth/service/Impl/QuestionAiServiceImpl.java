package org.example.tlonlineedupbackend.auth.service.Impl;

import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import org.example.tlonlineedupbackend.auth.dto.QuestionCreateDTO;
import org.example.tlonlineedupbackend.auth.entity.QuestionAI;
import org.example.tlonlineedupbackend.auth.repository.QuestionAiRepository;
import org.example.tlonlineedupbackend.auth.service.QuestionAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionAiServiceImpl implements QuestionAiService {

    @Autowired
    private QuestionAiRepository questionRepository;

    @Override
    @Transactional
    public QuestionAI createQuestion(QuestionCreateDTO dto) {
        QuestionAI question = new QuestionAI();
        question.setCourseId(dto.getCourseId());
        question.setStudentId(dto.getStudentId());
        question.setContent(dto.getContent());
        question.setKeywords(new Gson().toJson(dto.getKeywords())); // 将关键词列表转为JSON字符串
        question.setCreateTime(LocalDateTime.now());
        question.setStatus(0); // 初始状态为未回答
        return questionRepository.save(question);
    }

    @Override
    public QuestionAI getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("问题不存在，ID: " + id));
    }

    @Override
    public List<QuestionAI> getQuestionsByCourseId(Long courseId) {
        return questionRepository.findByCourseId(courseId);
    }

    @Override
    public List<QuestionAI> getQuestionsByStudentId(String studentId) {
        return questionRepository.findByStudentId(studentId);
    }

    @Override
    @Transactional
    public QuestionAI updateQuestionStatus(Long questionId, Integer status) {
        QuestionAI question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("问题不存在，ID: " + questionId));
        question.setStatus(status);
        return questionRepository.save(question);
    }

    @Override
    public Object getQuestionContent(Long questionId) {
        Optional<QuestionAI> byId = questionRepository.findById(questionId);
        return byId.map(QuestionAI::getContent).orElse(null);
    }
}
