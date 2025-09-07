package org.example.tlonlineedupbackend.auth.service.Impl;

import org.example.tlonlineedupbackend.auth.entity.User;
import org.example.tlonlineedupbackend.auth.repository.UserRepository;
import org.example.tlonlineedupbackend.auth.security.CustomUserDetails;
import org.example.tlonlineedupbackend.auth.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Optional;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private PasswordEncoder passwordEncoder; //注入BCrypt编码器

    private final UserRepository userRepository;

    public LoginServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails validateLogin(String phone, String password) {
        Optional<User> userOptional = userRepository.findByPhone(phone);
        if (userOptional.isPresent()) {
            User entityUser = userOptional.get();
            if (entityUser.getPassword_reset_required()) {
                throw new BadCredentialsException("Password Reset Required");
            }

            if (passwordEncoder.matches(password, entityUser.getPassword())) {
                return new CustomUserDetails(entityUser.getId(), entityUser.getUserName(), entityUser.getPhone(), entityUser.getAge(), entityUser.getSex(), entityUser.getIdentifiers(), entityUser.getAscription(), entityUser.getBirthday());
            }
        }
        throw new UsernameNotFoundException("Invalid phone number or password");
    }
}
