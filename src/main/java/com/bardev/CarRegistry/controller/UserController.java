package com.bardev.CarRegistry.controller;

import com.bardev.CarRegistry.controller.dto.LoginRequest;
import com.bardev.CarRegistry.controller.dto.SignUpRequest;
import com.bardev.CarRegistry.repository.entity.UserEntity;
import com.bardev.CarRegistry.service.impl.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping
@Slf4j
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

    // ADD USER IMAGE
    @PreAuthorize("hasAnyRole('CLIENT', 'VENDOR')")
    @PostMapping(value = "/addUserImage/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addUserImage(@PathVariable Long id, @RequestParam(value = "image") MultipartFile file ){
        if (file.isEmpty()){
            log.error("The file is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        }

        if(Objects.requireNonNull(file.getOriginalFilename()).endsWith(".jpg")){
            log.info("File name: {}", file.getOriginalFilename());

            // Add user image
            authenticationService.addImage(id, file);
            log.info("Image successfully saved.");
            return ResponseEntity.ok("Image successfully saved.");
        }

        log.error("The file is not a jpg file");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The file is not a jpg file");

    }

    // GET USER IMAGE
    @PreAuthorize("hasAnyRole('CLIENT', 'VENDOR')")
    @GetMapping(value = "/downloadUserImage/{id}")
    public ResponseEntity<byte[]> downloadUserImage(@PathVariable Long id){

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            byte[] image = authenticationService.downloadImage(id);

            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        }catch (Exception e){
            log.error("error downloading image");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

}
