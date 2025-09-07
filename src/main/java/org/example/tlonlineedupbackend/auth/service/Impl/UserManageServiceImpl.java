package org.example.tlonlineedupbackend.auth.service.Impl;


import jakarta.transaction.Transactional;
import org.apache.commons.codec.digest.DigestUtils;
import org.example.tlonlineedupbackend.auth.entity.User;
import org.example.tlonlineedupbackend.auth.repository.UserRepository;
import org.example.tlonlineedupbackend.auth.service.UserManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserManageServiceImpl implements UserManageService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public User createUser(User user) {
        user.setId(UUID.randomUUID().toString());
        String encodedPassword = DigestUtils.sha256Hex("123");
        user.setPassword(encodedPassword); // 默认密码
        user.setIdentifiers("Public"); // 固定标识
        user.setCreate_time(new Date());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public List<User> batchCreateUsers(List<User> users) {
        users.forEach(user -> {
            user.setId(UUID.randomUUID().toString());
            String encodedPassword = DigestUtils.sha256Hex("123");
            user.setPassword(encodedPassword); //
            user.setIdentifiers("Public");
            user.setCreate_time(new Date());
        });
        return userRepository.saveAll(users);
    }

    @Override
    @Transactional
    public User updateUser(String id, User user) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        existing.setUserName(user.getUserName());
        existing.setPhone(user.getPhone());
        existing.setSex(user.getSex());
        return userRepository.save(existing);
    }

    @Override
    @Transactional
    public void batchDeleteUsers(ArrayList<String> ids) {
        userRepository.deleteAllById(ids);
    }
}
