package com.example.common.dto;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Nullable;

@Validated
public record UserUpdateDTO(
        @Nullable String newUsername,
        @Nullable String newEmail,
        @Nullable @Length(min = 8, message = "Password must be at least 8 characters") String newPassword) {

}
