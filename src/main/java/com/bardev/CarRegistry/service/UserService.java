package com.bardev.CarRegistry.service;

import com.bardev.CarRegistry.repository.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    UserEntity save(UserEntity newUser);
    UserDetailsService userDetailsService();
}
