package com.example.gateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.common.dto.UserRegisterDTO;
import com.example.common.dto.UserResponseDTO;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @PostMapping("/api/user-service/users/register")
    ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRegisterDTO dto);
}
