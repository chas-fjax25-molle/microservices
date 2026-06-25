package com.example.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.dto.UserRegisterDTO;
import com.example.common.dto.UserResponseDTO;
import com.example.gateway.service.UserService;

@RestController
@RequestMapping("/api/gateway/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody @Validated UserRegisterDTO dto) {
        return userService.registerUser(dto);
    }
}
