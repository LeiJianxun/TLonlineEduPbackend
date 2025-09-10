package org.example.tlonlineedupbackend.auth.controller.LoginController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.codec.digest.DigestUtils;
import org.example.tlonlineedupbackend.auth.controller.UserInfoController.UserChangeRequest;
import org.example.tlonlineedupbackend.auth.entity.Department;
import org.example.tlonlineedupbackend.auth.entity.User;
import org.example.tlonlineedupbackend.auth.repository.DepartmentRepository;
import org.example.tlonlineedupbackend.auth.repository.UserRepository;
import org.example.tlonlineedupbackend.auth.security.CustomUserDetails;
import org.example.tlonlineedupbackend.auth.service.CaptchaService;
import org.example.tlonlineedupbackend.auth.service.UserService;
import org.example.tlonlineedupbackend.auth.service.LoginService;
import org.example.tlonlineedupbackend.auth.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    public CaptchaService captchaService;

    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest request, @RequestParam("phone") String phoneNumber, @RequestParam("password") String password, @RequestParam("captchaId") String captchaId, @RequestParam("captchaCode") String captchaCode) {
        try {
            //验证码验证
            ResponseEntity<Map<String, Object>> captchaResult =
                    captchaService.verifyCaptcha(captchaId, captchaCode);

            if (!captchaResult.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.badRequest().body("验证码验证失败");
            }

            CustomUserDetails userDetails = (CustomUserDetails) loginService.validateLogin(phoneNumber, password);
            HttpSession session = request.getSession();
            session.setAttribute("user", userDetails);
            session.setMaxInactiveInterval(-1);
            redisUtil.addOnlineUser(userDetails.getId());
            return new ResponseEntity<>(userDetails, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            // 处理密码重置需求
            if ("Password Reset Required".equals(e.getMessage())) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "PASSWORD_RESET_REQUIRED");
                response.put("message", "需要重置密码");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false); // 不创建新session
            if (session != null) {
                CustomUserDetails userDetails = (CustomUserDetails) session.getAttribute("user");
                if (userDetails != null) {
                    redisUtil.removeOfflineUser(userDetails.getId());
                }
                session.invalidate(); // 完全销毁session
            }
            // 无论session是否存在都返回成功，因为注销的目的就是清除状态
            return new ResponseEntity<>("注销成功", HttpStatus.OK);
        } catch (Exception e) {
            // 即使出现异常也返回成功，避免前端跳转到错误页面
            return new ResponseEntity<>("注销成功", HttpStatus.OK);
        }
    }

    @PostMapping("/changeUser")
    public ResponseEntity<CustomUserDetails> changeUser(HttpServletRequest request, @RequestBody UserChangeRequest userChangeRequest) {
        try {
            // 从 session 获取当前用户数据
            HttpSession session = request.getSession();
            CustomUserDetails currentUser = (CustomUserDetails) session.getAttribute("user");

            if (currentUser == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            // 修改密码
            if (userChangeRequest.getNewPassword() != null && !userChangeRequest.getSourcePassword().isEmpty()) {
                // 获取原密码的哈希
                String hashedPassword = DigestUtils.sha256Hex(userChangeRequest.getSourcePassword());

                Optional<User> userOptional = userRepository.findByPhone(currentUser.getPhone());
                User entityUser = userOptional.get();
                // 比较原密码的哈希值
                if (hashedPassword.equals(entityUser.getPassword())) {
                    // 原密码正确，生成新密码的哈希值
                    String hashedPassword2 = DigestUtils.sha256Hex(userChangeRequest.getNewPassword());

                    // 更新用户密码，假设您有一个方法来更新密码
                    boolean isPasswordUpdated = userService.updatePassword(currentUser.getPhone(), hashedPassword2);
                    if (!isPasswordUpdated) {
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }

                    // 更新 session 中的用户信息（包含新密码）
                    currentUser.setPassword(hashedPassword2);
                    // 密码更新成功后清楚session 重新登录
                    session.removeAttribute("user");
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 密码不正确
                }
            }

            // 仅更新用户信息
            CustomUserDetails updatedUser = userService.updateUserInfo(currentUser.getPhone(), userChangeRequest);
            session.setAttribute("user", updatedUser);

            // 返回更新后的用户信息
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/EditUser")
    public ResponseEntity<CustomUserDetails> EditUser(HttpServletRequest request, @RequestBody UserChangeRequest userChangeRequest) {
        try {
            // 从 session 获取当前用户
            HttpSession session = request.getSession();
            CustomUserDetails currentUser = (CustomUserDetails) session.getAttribute("user");

            if (currentUser == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }


            // 仅更新用户信息
            CustomUserDetails updatedUser = userService.updateUserInfo(userChangeRequest.getPhone(), userChangeRequest);
            // SHA-256 哈希密码长度为 64 个字符
            if (userChangeRequest.getPassword() != null && userChangeRequest.getPassword().length() == 64) {
                // 检查密码是否只包含合法的十六进制字符
                if (userChangeRequest.getPassword().matches("[a-fA-F0-9]{64}")) {
                    userService.updatePassword(userChangeRequest.getPhone(), userChangeRequest.getPassword());
                }
            } else {
                String hashedPassword = DigestUtils.sha256Hex(userChangeRequest.getPassword());
                userService.updatePassword(userChangeRequest.getPhone(), hashedPassword);
            }
            session.setAttribute("user", updatedUser);

            // 返回更新后的用户信息
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/classUser")
    public ResponseEntity<Map<String, Object>> getClassUserWithDepartments(
            @RequestParam(required = false) String keyword, // 新增关键字参数
            HttpServletRequest request
    ) {
        try {
            // 1. 获取当前用户
            HttpSession session = request.getSession();
            CustomUserDetails currentUser = (CustomUserDetails) session.getAttribute("user");
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // 2. 获取用户关联的部门ID
            Long departmentId = currentUser.getAscription(); // 假设 ascription 存储的是部门ID
            if (departmentId == null) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "用户未关联部门"));
            }

            // 3. 查询部门信息
            Optional<Department> departmentOpt = departmentRepository.findById(departmentId);
            if (!departmentOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "部门不存在"));
            }

            Department department = departmentOpt.get();

            // 4. 查询所有子部门（支持关键字筛选）
            List<Department> subDepartments;
            if (keyword != null && !keyword.isEmpty()) {
                // 根据关键字筛选子部门
                subDepartments = departmentRepository.findByParentIdAndNameContaining(
                        departmentId, keyword
                );
            } else {
                // 查询所有子部门
                subDepartments = departmentRepository.findByParentIdAndParentIdNotNull(departmentId);
            }

            // 5. 构建返回数据
            Map<String, Object> response = new HashMap<>();
            response.put("userInfo", currentUser);
            response.put("department", department); // 主部门信息
            response.put("subDepartments", subDepartments); // 子部门列表

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("error", "服务器内部错误"));
        }
    }

    @GetMapping("/classUser/{d_id}")
    public ResponseEntity<Map<String, Object>> getClassUserWithDepartmentsUserId(@PathVariable Long d_id) {
        try {

            // 3. 查询子部门下账号
            List<User> byAscriptionIn = userRepository.findByAscriptionIn(Collections.singletonList(d_id));

            // 5. 构建返回数据
            Map<String, Object> response = new HashMap<>();
            response.put("userInfo", byAscriptionIn);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("error", "服务器内部错误"));
        }
    }

}


