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
@Table(name = "question_ai")
public class QuestionAI {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long courseId;
    private String studentId;
    private String content;
    private String keywords; // JSON数组存储
    private LocalDateTime createTime;
    private Integer status; // 0-未回答 1-已回答
}
