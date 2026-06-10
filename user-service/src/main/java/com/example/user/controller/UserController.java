package com.example.user.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.dto.LoginRequestDTO;
import com.example.common.dto.UserRegisterDTO;
import com.example.common.dto.UserResponseDTO;
import com.example.common.dto.UserUpdateDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user-service/users")
@RequiredArgsConstructor
@Data
public class UserController {
    @PostMapping("/validate")
    public ResponseEntity<UserResponseDTO> validateLogin(@RequestBody @Validated LoginRequestDTO request) {
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable @NotNull @NonNull UUID id) {
        return ResponseEntity.internalServerError().build();
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody @Validated UserRegisterDTO entity) {
        return ResponseEntity.internalServerError().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable @NotNull @NonNull UUID id,
            @RequestBody @Validated UserUpdateDTO entity) {
        return ResponseEntity.internalServerError().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponseDTO> deleteUser(@PathVariable @NotNull @NonNull UUID id) {
        return ResponseEntity.internalServerError().build();
    }
}
