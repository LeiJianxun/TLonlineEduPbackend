package org.example.tlonlineedupbackend.auth.service;

import org.example.tlonlineedupbackend.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface AllUserService {
    Page<User> getAllUser(Pageable pageable);
    void deleteUser(ArrayList<String> ids);
    Page<User> getUserByAscription(Long ascription, Pageable pageable);
}
