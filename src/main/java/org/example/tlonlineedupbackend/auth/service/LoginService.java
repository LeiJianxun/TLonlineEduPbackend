package org.example.tlonlineedupbackend.auth.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface LoginService {
    UserDetails validateLogin(String phone, String password);
}
