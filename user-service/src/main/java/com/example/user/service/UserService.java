package com.example.user.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.common.dto.LoginRequestDTO;
import com.example.common.dto.UserRegisterDTO;
import com.example.common.dto.UserResponseDTO;
import com.example.common.dto.UserUpdateDTO;
import com.example.common.security.JwtUtil;
import com.example.user.exception.DuplicateUserException;
import com.example.user.exception.UserNotFoundException;
import com.example.user.model.User;
import com.example.user.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO validateUser(LoginRequestDTO loginRequest) {
        if (loginRequest.username().isEmpty() || loginRequest.password().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password cannot be blank");
        }
        User user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail());
    }

    public UserResponseDTO registerUser(UserRegisterDTO userRegisterDTO) {
        if (userRepository.existsByUsername(userRegisterDTO.username())) {
            throw new DuplicateUserException("username", userRegisterDTO.username());
        }
        if (userRepository.existsByEmail(userRegisterDTO.email())) {
            throw new DuplicateUserException("email", userRegisterDTO.email());
        }

        User user = new User();
        user.setUsername(userRegisterDTO.username());
        user.setEmail(userRegisterDTO.email());
        user.setPassword(passwordEncoder.encode(userRegisterDTO.password()));

        User savedUser = userRepository.save(user);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail());
    }

    public UserResponseDTO getUserById(UUID id) {
        return userRepository.findById(id)
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail()))
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public UserResponseDTO updateUser(UUID id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (userUpdateDTO.newUsername() != null) {
            user.setUsername(userUpdateDTO.newUsername());
        }
        if (userUpdateDTO.newEmail() != null) {
            user.setEmail(userUpdateDTO.newEmail());
        }
        if (userUpdateDTO.newPassword() != null) {
            user.setPassword(passwordEncoder.encode(userUpdateDTO.newPassword()));
        }

        User updatedUser = userRepository.save(user);

        return new UserResponseDTO(
                id,
                updatedUser.getUsername(),
                updatedUser.getEmail());
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
