package org.example.tlonlineedupbackend.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "exercise_answer")
public class ExerciseAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_task_id")
    @Getter
    @Setter
    private StudentTask studentTask;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @Getter
    @Setter
    private ExerciseQuestion question;

    @Lob
    @Getter
    @Setter
    private String answer; // 存储格式：单选存选项ID，多选存逗号分隔ID，主观题存文本

    // Getters and Setters
}