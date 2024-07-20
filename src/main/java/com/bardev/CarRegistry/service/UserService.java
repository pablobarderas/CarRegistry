package com.bardev.CarRegistry.service;

import com.bardev.CarRegistry.repository.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserEntity save(UserEntity newUser);
    UserDetailsService userDetailsService();
    void addImage(Long id, MultipartFile image);
    byte[] downloadImage(Long id);
}
