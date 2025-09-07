package org.example.tlonlineedupbackend.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exercise_question")
public class ExerciseQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    @Getter
    @Setter
    private CourseTask task;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Getter
    @Setter
    private QuestionType type;

    @Lob
    @Column(nullable = false)
    @Getter
    @Setter
    private String question;

    @Lob
    @Getter
    @Setter
    private String analysis;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private List<QuestionOption> options = new ArrayList<>();

    // 题目类型枚举
    public enum QuestionType {
        SINGLE, MULTIPLE, SUBJECTIVE
    }
}