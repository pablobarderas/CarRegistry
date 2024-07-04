package com.bardev.users_example.service;

import com.bardev.users_example.entities.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

public interface UserService {
    public UserEntity save(UserEntity newUser);
    UserDetailsService userDetailsService();
}
