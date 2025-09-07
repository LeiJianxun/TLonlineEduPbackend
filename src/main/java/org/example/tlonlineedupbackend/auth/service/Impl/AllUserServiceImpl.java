package org.example.tlonlineedupbackend.auth.service.Impl;

import org.example.tlonlineedupbackend.auth.entity.User;
import org.example.tlonlineedupbackend.auth.repository.UserRepository;
import org.example.tlonlineedupbackend.auth.service.AllUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AllUserServiceImpl implements AllUserService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public Page<User> getAllUser(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public void deleteUser(ArrayList<String> ids) {
        for (String id : ids) {
            userRepository.deleteById(id);
        }
    }

    @Override
    public Page<User> getUserByAscription(Long ascription, Pageable pageable){
        return (Page<User>) userRepository.findByAscriptionIn(Collections.singletonList(ascription));
    }

}
