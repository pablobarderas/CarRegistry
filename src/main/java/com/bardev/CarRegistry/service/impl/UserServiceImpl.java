package com.bardev.CarRegistry.service.impl;

import com.bardev.CarRegistry.repository.UserRepository;
import com.bardev.CarRegistry.repository.entity.UserEntity;
import com.bardev.CarRegistry.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserEntity save(UserEntity newUser) {
        return userRepository.save(newUser);
    }

    @Override
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }

    @Override
    public void addImage(Long id, MultipartFile image) {

        UserEntity user = userRepository.findById(id).orElseThrow(()-> new RuntimeException("User not found"));

        log.info("Saving user image...");

        // Encode and set image
        try{
            user.setImage(image.getBytes());
            userRepository.save(user);
            log.info("Image save successfully for user {}", id);
        }catch (IOException e){
            log.error("Error adding image for user id: {}", id, e);
            throw new RuntimeException("Failed to save image.", e);
        }


    }

    @Override
    public byte[] downloadImage(Long id) {

        UserEntity demoEntity = userRepository.findById(id).orElseThrow(RuntimeException::new);

        log.info("Getting user image...");

        // Decode and set image
        return demoEntity.getImage();
    }


}
