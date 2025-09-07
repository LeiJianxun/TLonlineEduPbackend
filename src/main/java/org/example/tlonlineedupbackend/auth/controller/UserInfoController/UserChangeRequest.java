package org.example.tlonlineedupbackend.auth.controller.UserInfoController;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Date;

public class UserChangeRequest {
    private String userName;
    private String sex;
    private String phone;
    @Getter
    @JsonProperty("SourcePassword") // 映射前端的字段
    private String sourcePassword;
    @Getter
    @JsonProperty("newPassword")// 映射前端的字段
    private String newPassword;
    private Date birthday;
    @Getter
    private String password;
    @Getter
    private String identifiers;

    // Getters and Setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
