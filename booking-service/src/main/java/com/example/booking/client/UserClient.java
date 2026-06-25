package com.example.booking.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.common.dto.UserResponseDTO;

import jakarta.validation.constraints.NotNull;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/user-service/users/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable @NotNull UUID id);
}
