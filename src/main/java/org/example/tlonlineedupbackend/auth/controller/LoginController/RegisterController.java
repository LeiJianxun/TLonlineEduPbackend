package org.example.tlonlineedupbackend.auth.controller.LoginController;

import lombok.Getter;
import lombok.Setter;
import org.example.tlonlineedupbackend.auth.service.CaptchaService;
import org.example.tlonlineedupbackend.auth.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @Autowired
    public CaptchaService captchaService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {

        //验证码验证
        ResponseEntity<Map<String, Object>> captchaResult =
                captchaService.verifyCaptcha(registerRequest.getCaptchaId(), registerRequest.getCaptchaCode());

        if (!captchaResult.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.badRequest().body("验证码验证失败");
        }

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
    @Getter
    @Setter
    private String captchaId;
    @Getter
    @Setter
    private String captchaCode;

}
