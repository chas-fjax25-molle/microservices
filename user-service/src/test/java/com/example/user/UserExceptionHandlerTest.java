package com.example.user;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.common.security.JwtUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class UserExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        when(jwtUtil.validateToken("admin-token")).thenReturn(true);
        when(jwtUtil.getIdFromToken("admin-token")).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        when(jwtUtil.getRoleFromToken("admin-token")).thenReturn(Optional.of("ADMIN"));
    }

    @Test
    void duplicateUsernameShouldReturn409() throws Exception {
        mockMvc.perform(post("/api/user-service/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"dupuser\", \"email\": \"dupuser@test.com\", \"password\": \"password123\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/user-service/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"dupuser\", \"email\": \"other@test.com\", \"password\": \"password123\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("User with username 'dupuser' already exists"));
    }

    @Test
    void duplicateEmailShouldReturn409() throws Exception {
        mockMvc.perform(post("/api/user-service/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"first\", \"email\": \"shared@test.com\", \"password\": \"password123\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/user-service/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"second\", \"email\": \"shared@test.com\", \"password\": \"password123\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("User with email 'shared@test.com' already exists"));
    }

    @Test
    void validationErrorShouldReturnCustomMessage() throws Exception {
        mockMvc.perform(post("/api/user-service/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"\", \"email\": \"test@test.com\", \"password\": \"password123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("username: Username is required"));
    }

    @Test
    void notFoundShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/user-service/users/00000000-0000-0000-0000-000000000000")
                .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found with id: 00000000-0000-0000-0000-000000000000"));
    }
}
