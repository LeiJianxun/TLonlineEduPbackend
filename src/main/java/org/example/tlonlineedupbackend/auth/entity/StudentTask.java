package org.example.tlonlineedupbackend.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student_task")
public class StudentTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @Getter
    @Setter
    private User student;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    @Getter
    @Setter
    private CourseTask task;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Getter
    @Setter
    private Status status = Status.NOT_STARTED;

    @Lob
    @Column(name = "submit_content")
    @Getter
    @Setter
    private String submitContent;

    @Getter
    @Setter
    private Integer score;

    @Column(name = "submit_time")
    @Getter
    @Setter
    private LocalDateTime submitTime;

    // 状态枚举
    public enum Status {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }

}
