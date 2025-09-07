package org.example.tlonlineedupbackend.auth.controller.UserInfoController;

import org.example.tlonlineedupbackend.auth.entity.User;
import org.example.tlonlineedupbackend.auth.service.UserManageService;
import org.example.tlonlineedupbackend.auth.util.CsvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/usersManage")
public class UserManageController {

    @Autowired
    private UserManageService userManageService;

    // 单条新增
    @PostMapping
    public ResponseEntity<User> createUser(
            @RequestBody User user,
            @RequestParam Long departmentId // 从前端获取当前部门ID
    ) {
        user.setAscription(departmentId);
        return ResponseEntity.ok(userManageService.createUser(user));
    }

    // 批量导入
    @PostMapping("/batch")
    public ResponseEntity<Integer> batchCreateUsers(
            @RequestBody List<User> users,
            @RequestParam Long departmentId
    ) {
        users.forEach(user -> user.setAscription(departmentId));
        return ResponseEntity.ok(userManageService.batchCreateUsers(users).size());
    }

    // 更新用户
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable String id,
            @RequestBody User user
    ) {
        return ResponseEntity.ok(userManageService.updateUser(id, user));
    }

    // 批量删除
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDeleteUsers(@RequestBody ArrayList<String> ids) {
        userManageService.batchDeleteUsers(ids);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<Integer> uploadUsers(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long departmentId
    ) {
        try {
            List<User> users = CsvUtil.parseUsers(file.getInputStream(), departmentId);
            return ResponseEntity.ok(userManageService.batchCreateUsers(users).size());
        } catch (IOException e) {
            throw new RuntimeException("文件处理失败", e);
        }
    }
}
