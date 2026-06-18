package com.example.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.dto.UserRegisterDTO;
import com.example.common.dto.UserResponseDTO;
import com.example.gateway.client.UserServiceClient;

@RestController
@RequestMapping("/api/gateway/users")
public class UserRegistrationController {

    private final UserServiceClient userServiceClient;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationController(UserServiceClient userServiceClient, PasswordEncoder passwordEncoder) {
        this.userServiceClient = userServiceClient;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody @Validated UserRegisterDTO dto) {
        UserRegisterDTO hashedDto = new UserRegisterDTO(
            dto.username(),
            dto.email(),
            passwordEncoder.encode(dto.password())
        );

        return userServiceClient.registerUser(hashedDto);
    }
}
