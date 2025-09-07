package org.example.tlonlineedupbackend.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
public class School {
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    @Column(name = "school_code")
    private String schoolCode;
    @Getter
    @Setter
    @Column(name = "school_zg")
    private String schoolZg;
    @Getter
    @Setter
    private String region;
    @Getter
    @Setter
    private String level;
    @Getter
    @Setter
    private String b1;
    @Getter
    @Setter
    private String b2;
    @Getter
    @Setter
    private String b3;

}
