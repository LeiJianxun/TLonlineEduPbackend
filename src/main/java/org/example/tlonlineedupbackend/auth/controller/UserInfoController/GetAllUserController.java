package org.example.tlonlineedupbackend.auth.controller.UserInfoController;

import org.example.tlonlineedupbackend.auth.entity.User;
import org.example.tlonlineedupbackend.auth.service.AllUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/users")
public class GetAllUserController {

    @Autowired
    private AllUserService allUserService;

    @GetMapping
    public ResponseEntity<Page<User>> getAllUser(Pageable pageable) {
        Page<User> users = allUserService.getAllUser(pageable);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestBody ArrayList<String> ids) {
        allUserService.deleteUser(ids); // 调用删除方法
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 返回无内容的响应
    }

}
