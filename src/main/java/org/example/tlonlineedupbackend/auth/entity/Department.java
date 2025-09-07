package org.example.tlonlineedupbackend.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
public class Department {
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
    private Date update_time;
    @Getter
    @Setter
    private String update_user;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    @Column(name = "d_code")
    private String DCode;
    @Getter
    @Setter
    @Column(name = "parent_id")
    private Long parentId;
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
