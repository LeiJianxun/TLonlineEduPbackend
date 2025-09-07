package org.example.tlonlineedupbackend.auth.controller.AIController;

import org.example.tlonlineedupbackend.auth.dto.QuestionCreateDTO;
import org.example.tlonlineedupbackend.auth.entity.AnswerAI;
import org.example.tlonlineedupbackend.auth.entity.QuestionAI;
import org.example.tlonlineedupbackend.auth.repository.QuestionAiRepository;
import org.example.tlonlineedupbackend.auth.service.AnswerAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/questionsAi")
public class QuestionAIController {

    @Autowired
    private QuestionAiRepository questionAiRepository;

    @Autowired
    private AnswerAiService answerService;

    @PostMapping
    public ResponseEntity<?> createQuestion(@RequestBody QuestionCreateDTO dto) {
        QuestionAI question = new QuestionAI();
        question.setContent(dto.getContent());
        question.setCreateTime(dto.getCreateTime());
        question.setCourseId(dto.getCourseId());
        question.setStudentId(dto.getStudentId());
        question.setStatus(0);
        question.setKeywords(dto.getKeywords().toString());

        return ResponseEntity.ok(questionAiRepository.save(question));
    }

    @GetMapping("/getAnswer/{id}")
    public ResponseEntity<?> getAnswer(@PathVariable Long id) {
        List<AnswerAI> answersByQuestionId = answerService.getAnswersByQuestionId(id);
        for (AnswerAI answerAI : answersByQuestionId) {
            if (answerAI.getScore() != null && answerAI.getEmotionAnalysis() != null){
                return ResponseEntity.ok(answerAI);
            }
        }
        return ResponseEntity.ok(null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestion(@PathVariable Long id) {
        Optional<QuestionAI> byId = questionAiRepository.findById(id);
        QuestionAI questionAI = byId.get();
        return ResponseEntity.ok(questionAI);
    }

    @GetMapping("/getStudent/{id}")
    public ResponseEntity<?> getQuestion(@PathVariable String id) {
        List<QuestionAI> byStudentId = questionAiRepository.findByStudentId(id);
        for (QuestionAI questionAI : byStudentId) {
            if (questionAI.getStatus() == 0) {
                return ResponseEntity.ok(questionAI);
            }
        }
        return ResponseEntity.ok(null);
    }
}
