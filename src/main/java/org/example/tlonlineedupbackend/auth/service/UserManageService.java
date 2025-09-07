package org.example.tlonlineedupbackend.auth.service;

import org.example.tlonlineedupbackend.auth.entity.User;

import java.util.ArrayList;
import java.util.List;

public interface UserManageService {
    User createUser(User user);
    List<User> batchCreateUsers(List<User> users);
    User updateUser(String id, User user);
    void batchDeleteUsers(ArrayList<String> ids);
}
