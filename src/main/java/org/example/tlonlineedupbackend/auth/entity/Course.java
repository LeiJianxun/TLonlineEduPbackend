package org.example.tlonlineedupbackend.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Course {
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
    @Setter
    private Date create_time;
    @Getter
    @Setter
    private String create_user;
    @Getter
    @Setter
    private Long ascription;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String ks;
    @Getter
    @Setter
    private String course_type;
    @Getter
    @Setter
    private String b1;
    @Getter
    @Setter
    private String b2;
    @Getter
    @Setter
    private String b3;

    // 新增关联关系
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseDepartment> departments = new ArrayList<>();
}
