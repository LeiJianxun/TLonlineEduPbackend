package org.example.tlonlineedupbackend.auth.controller.LoginController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.tlonlineedupbackend.auth.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Autowired
    public CaptchaService captchaService;

    /**
     * 验证码图片生成
     */
    @GetMapping("/generate")
    public void generateCaptcha(HttpServletResponse response) throws IOException {
        captchaService.generateCaptcha(response);
    }

    /**
     * 验证码验证
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyCaptcha(@RequestParam String captchaId, @RequestParam String captchaCode) {
        return captchaService.verifyCaptcha(captchaId, captchaCode);
    }

}
