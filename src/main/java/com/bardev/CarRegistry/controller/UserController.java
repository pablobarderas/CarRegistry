package com.bardev.users_example.rest;

import com.bardev.users_example.dto.LoginRequest;
import com.bardev.users_example.dto.SignUpRequest;
import com.bardev.users_example.service.impl.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationService authenticationService;


    // SIGNUP
    @PostMapping("/signup")
    public ResponseEntity<?> singUp(@RequestBody SignUpRequest request){

        try{
            return ResponseEntity.ok(authenticationService.signUp(request));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        try{
            return ResponseEntity.ok(authenticationService.login(request));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


}