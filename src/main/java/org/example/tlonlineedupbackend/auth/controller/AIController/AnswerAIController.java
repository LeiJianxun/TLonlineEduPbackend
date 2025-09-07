package org.example.tlonlineedupbackend.auth.controller.AIController;

import org.example.tlonlineedupbackend.auth.dto.AnswerSubmitDTO;
import org.example.tlonlineedupbackend.auth.entity.AnswerAI;
import org.example.tlonlineedupbackend.auth.service.AIService;
import org.example.tlonlineedupbackend.auth.service.AnswerAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/answersAi")
public class AnswerAIController {

    @Autowired
    private AnswerAiService answerService;

    @Autowired
    private AIService aiService;

    @PostMapping
    public ResponseEntity<?> submitAnswer(@RequestBody AnswerSubmitDTO dto) {
        // 保存答案
        AnswerAI answer = answerService.submitAnswer(dto);

        // 调用AI评分
        aiService.evaluateAnswer(answer);
        return ResponseEntity.ok(answer);
    }

}
