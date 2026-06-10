package com.example.common.dto;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
public record LoginRequestDTO(
        @NotBlank(message = "username cannot be blank") String username,
        @NotBlank(message = "password cannot be blank") String password) {
}
