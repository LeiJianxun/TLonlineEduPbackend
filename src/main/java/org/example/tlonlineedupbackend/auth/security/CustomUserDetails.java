package org.example.tlonlineedupbackend.auth.security;

import lombok.Getter;
import lombok.Setter;
import org.example.tlonlineedupbackend.auth.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CustomUserDetails implements UserDetails {
    @Getter
    private String id;

    private String username;
    @Getter
    private String phone;
    @Getter
    private Integer age;
    @Getter
    private String sex;
    @Getter
    private String Identifiers;
    @Getter
    private Long ascription;
    @Getter
    private Date birthday;
    @Setter
    @Getter
    private String password;

    public CustomUserDetails(String id ,String username, String phone, Integer age, String sex, String Identifiers, Long ascription, Date birthday) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.age = age;
        this.sex = sex;
        this.Identifiers = Identifiers;
        this.ascription = ascription;
        this.birthday = birthday;
    }

    public User toUser() {
        User user = new User();
        user.setId(this.id);
        user.setUserName(this.username);
        user.setPhone(this.phone);
        user.setAge(this.age);
        user.setSex(this.sex);
        user.setIdentifiers(this.Identifiers);
        user.setAscription(this.ascription);
        user.setBirthday(this.birthday);
        user.setPassword(this.password);
        return user;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public boolean equals(Object o) {
        System.out.println("[DEBUG] CustomUserDetails.equals called");
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomUserDetails that = (CustomUserDetails) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        System.out.println("[DEBUG] CustomUserDetails.hashCode called");
        return Objects.hash(id);
    }

}
