package org.example.tlonlineedupbackend.auth.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "offline_schedule")
public class OfflineSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @OneToOne
    @JoinColumn(name = "task_id", nullable = false)
    @Getter
    @Setter
    private CourseTask courseTask;

    @Column(nullable = false, length = 50)
    @Getter
    @Setter
    private String classroom;

    @Column(name = "schedule_time", nullable = false)
    @Getter
    @Setter
    private String scheduleTime;

    @Column(nullable = false)
    @Getter
    @Setter
    private Integer duration; // 单位：分钟

}
