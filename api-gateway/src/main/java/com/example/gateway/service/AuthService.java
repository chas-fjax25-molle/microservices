package com.example.gateway.service;

import java.nio.file.AccessDeniedException;

import org.springframework.stereotype.Service;

import com.example.common.dto.LoginRequestDTO;
import com.example.common.dto.UserResponseDTO;
import com.example.common.security.JwtUtil;
import com.example.gateway.client.UserServiceClient;

@Service
public class AuthService {

    private final UserServiceClient userServiceClient;
    private final JwtUtil jwtUtil;

    public AuthService(UserServiceClient userServiceClient, JwtUtil jwtUtil) {
        this.userServiceClient = userServiceClient;
        this.jwtUtil = jwtUtil;
    }

    public String login(LoginRequestDTO dto) throws AccessDeniedException {

        UserResponseDTO user = userServiceClient.validateLogin(dto).getBody();

        if (user == null) {
            throw new AccessDeniedException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.username(), user.id(), user.role());
    }

}