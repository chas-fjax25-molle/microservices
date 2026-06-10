package com.example.user.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import com.example.common.dto.LoginRequestDTO;
import com.example.common.dto.UserRegisterDTO;
import com.example.common.dto.UserResponseDTO;
import com.example.common.dto.UserUpdateDTO;
import com.example.user.service.UserService;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

@RestController
@RequestMapping("/api/user-service/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Validate that the login credentials are valid and return the user details if
     * they are.
     * 
     * @param request Login credentials
     * @return The user details if the login credentials are valid
     */
    @PostMapping("/validate")
    public ResponseEntity<UserResponseDTO> validateLogin(@RequestBody @Validated LoginRequestDTO request) {
        return ResponseEntity.ok(userService.validateUser(request));
    }

    /**
     * Get the user details for a given ID.
     * 
     * @param id The ID of the user to get
     * @return The user details for the given ID if it exists
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable @NotNull @NonNull UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Register a new user.
     * 
     * @param entity The user details to register
     * @return The user details for the newly registered user
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody @Validated UserRegisterDTO entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(entity));
    }

    /**
     * Update the user details for a given ID.
     * 
     * @param id     The ID of the user to update
     * @param entity Structure containing the updated user details
     * @return The updated user details for the given ID if it exists
     */
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable @NotNull @NonNull UUID id,
            @RequestBody @Validated UserUpdateDTO entity) {
        return ResponseEntity.ok(userService.updateUser(id, entity));
    }

    /**
     * Delete the user with the specified ID.
     * 
     * @param id The ID of the user to delete
     * @return A response indicating that the user has been deleted successfully
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @NotNull @NonNull UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
