package com.example.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.common.security.JwtUtil;

@SpringBootTest
class UserServiceApplicationTests {
    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void contextLoads() {
    }
}