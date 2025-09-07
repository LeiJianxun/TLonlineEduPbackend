package org.example.tlonlineedupbackend.auth.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Entity
public class User {
    @Setter
    @Getter
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Getter
    @Setter
    private String userName;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private String sex;
    @Getter
    @Setter
    private Integer age;
    @Getter
    @Setter
    private String phone;
    @Getter
    @Setter
    private Date create_time;
    @Getter
    @Setter
    private Date last_login_time;
    @Getter
    @Setter
    private String Identifiers;
    @Getter
    @Setter
    private Long ascription;
    @Getter
    @Setter
    private String b1;
    @Getter
    @Setter
    private String b2;
    @Getter
    @Setter
    private String b3;
    @Getter
    @Setter
    private Date birthday;
    @Getter
    @Setter
    private Boolean password_reset_required;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}