package org.example.tlonlineedupbackend.auth.controller.LoginController;

import lombok.Data;
import org.example.tlonlineedupbackend.auth.entity.User;
import org.example.tlonlineedupbackend.auth.repository.UserRepository;
import org.example.tlonlineedupbackend.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class PasswordResetController {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        try {
            Optional<User> userOptional = userRepository.findByPhone(request.getPhone());
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("用户不存在");
            }
            User user = userOptional.get();

            // 旧密码验证-sha256Hex
            if (user.getPassword_reset_required()) {
                String oldPassword = DigestUtils.sha256Hex(request.getOldPassword());
                if (!oldPassword.equals(user.getPassword())){
                    return ResponseEntity.badRequest().body("原密码错误");
                }
            }

            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()) && !user.getPassword_reset_required()){
                return ResponseEntity.badRequest().body("原密码错误");
            }

            // 新加密密码生成
            String newPassword = passwordEncoder.encode(request.getNewPassword());
            user.setPassword(newPassword);
            user.setPassword_reset_required(false);
            userRepository.save(user);

            return ResponseEntity.ok("密码重置成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("密码重置失败" + e.getMessage());
        }
    }
}

@Data
class PasswordResetRequest {
    private String username;
    private String phone;
    private String oldPassword;
    private String newPassword;
}
