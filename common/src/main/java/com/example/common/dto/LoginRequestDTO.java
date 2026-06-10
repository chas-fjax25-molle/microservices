package com.example.common.dto;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
public record LoginRequestDTO(
        @NotBlank(message = "account name cannot be blank") String accountName,
        @NotBlank(message = "password cannot be blank") String password) {
}
