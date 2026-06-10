package com.example.common.dto;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;

@Validated
public record UserRegisterDTO(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Email is required") String email,
        @NotBlank(message = "Password is required") @Length(min = 8, message = "Password must be at least 8 characters long") String password) {

}
