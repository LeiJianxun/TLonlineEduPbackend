package org.example.tlonlineedupbackend.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String originalName;
    @Getter
    @Setter
    private String storedName;
    @Getter
    @Setter
    private String filePath;
    @Getter
    @Setter
    private Long fileSize;
    @Getter
    @Setter
    private LocalDateTime uploadTime;


    @ManyToOne
    @JoinColumn(name = "task_id")
    @Getter
    @Setter
    private CourseTask courseTask;

    @ManyToOne
    @JoinColumn(name = "uploader_id")
    @Getter
    @Setter
    private User uploader;

}
