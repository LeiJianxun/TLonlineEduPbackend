package org.example.tlonlineedupbackend.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course_task")
public class CourseTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @Getter
    @Setter
    private Course course;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    @Getter
    @Setter
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false, length = 20)
    @Getter
    @Setter
    private TaskType taskType;

    @Column(nullable = false, length = 100)
    @Getter
    @Setter
    private String title;

    @Lob
    @Getter
    @Setter
    private String content;

    @Column(name = "start_time")
    @Getter
    @Setter
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    @Getter
    @Setter
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    @Getter
    @Setter
    private User creator;

    @OneToMany(mappedBy = "courseTask", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private List<Attachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    @Getter
    @Setter
    private List<StudentTask> studentTasks = new ArrayList<>();

    @OneToOne(mappedBy = "courseTask", cascade = CascadeType.ALL)
    @Getter
    @Setter
    private OfflineSchedule offlineSchedule;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private List<ExerciseQuestion> exerciseQuestions = new ArrayList<>();

    @Column(name = "not_started_count")
    @Getter
    @Setter
    private Integer notStartedCount;

    @Column(name = "in_progress_count")
    @Getter
    @Setter
    private Integer inProgressCount;

    @Column(name = "completed_count")
    @Getter
    @Setter
    private Integer completedCount;

    // 枚举类型定义
    public enum TaskType {
        HOMEWORK, VIDEO, EXERCISE, OFFLINE
    }

}
