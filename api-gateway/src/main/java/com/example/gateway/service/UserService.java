package com.example.gateway.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.common.dto.UserRegisterDTO;
import com.example.common.dto.UserResponseDTO;
import com.example.gateway.client.UserServiceClient;

@Service
public class UserService {

    private final UserServiceClient userServiceClient;

    public UserService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    public ResponseEntity<UserResponseDTO> registerUser(UserRegisterDTO dto) {
        return userServiceClient.registerUser(dto);
    }
    
}
