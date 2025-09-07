package org.example.tlonlineedupbackend.auth.service;

import java.util.Date;

public interface RegisterService {
    boolean registerUser(String phone, String password, String name, Date birthday, String identifiers);
}
