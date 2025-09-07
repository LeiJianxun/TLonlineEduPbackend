package org.example.tlonlineedupbackend.auth.controller.LoginController;

import lombok.Getter;
import lombok.Setter;
import org.example.tlonlineedupbackend.auth.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {
        boolean isRegistered = registerService.registerUser(
                registerRequest.getPhone(),
                registerRequest.getPassword(),
                registerRequest.getName(),
                registerRequest.getBirthday(),
                registerRequest.getIdentifiers()
        );

        if (isRegistered) {
            return ResponseEntity.status(HttpStatus.CREATED).body("注册成功");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("用户已存在或注册失败");
        }
    }
}

class RegisterRequest {
    @Getter
    @Setter
    private String phone;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String identifiers;
    @Getter
    @Setter
    private Date birthday;

}
