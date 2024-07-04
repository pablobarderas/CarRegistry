package com.bardev.CarRegistry.service.impl;

import com.bardev.CarRegistry.controller.dto.JwtResponse;
import com.bardev.CarRegistry.controller.dto.LoginRequest;
import com.bardev.CarRegistry.controller.dto.SignUpRequest;
import com.bardev.CarRegistry.repository.UserRepository;
import com.bardev.CarRegistry.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // SIGNUP
    public JwtResponse signUp(SignUpRequest request) throws BadRequestException{

        var user = UserEntity
                .builder()
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_CLIENT")
                .build();

        // Save user with and generated token
        user = userService.save(user);
        var jwt = jwtService.generateToken(user);

        return JwtResponse.builder()
                .token(jwt)
                .build();

    }

    // LOGIN
    public JwtResponse login(LoginRequest request){

        // Check username, password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new IllegalArgumentException("Invalid email or password."));

        // Token generate for authenticated user
        var jwt = jwtService.generateToken(user);

        return JwtResponse
                .builder()
                .token(jwt)
                .build();

    }

}
