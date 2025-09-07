package org.example.tlonlineedupbackend.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Data
@Getter
@Setter
@Table(name = "answer_ai")
public class AnswerAI {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long questionId;
    private String content;
    private Double score;
    private String emotionAnalysis;
    private String emotionAnalysisColor;
    private LocalDateTime submitTime;
}
