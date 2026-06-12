package com.example.common.security;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    private static KeyPair keyPair;
    private static KeyPair wrongKeyPair;

    @BeforeAll
    static void setUp() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        keyPair = generator.generateKeyPair();
        wrongKeyPair = generator.generateKeyPair();
    }

    @Test
    void generateTokenAndValidate() {
        JwtUtil jwtUtil = new JwtUtil(keyPair.getPublic(), keyPair.getPrivate());
        String token = jwtUtil.generateToken("alice", UUID.fromString("00000000-0000-0000-0000-000000000001"));
        assertThat(jwtUtil.validateToken(token)).isTrue();
    }

    @Test
    void getUsernameFromToken() {
        JwtUtil jwtUtil = new JwtUtil(keyPair.getPublic(), keyPair.getPrivate());
        String token = jwtUtil.generateToken("bob", UUID.randomUUID());
        assertThat(jwtUtil.getUsernameFromToken(token)).isEqualTo("bob");
    }

    @Test
    void validateToken_withWrongKey_returnsFalse() {
        JwtUtil jwtUtil = new JwtUtil(keyPair.getPublic(), keyPair.getPrivate());
        String token = jwtUtil.generateToken("mallory", UUID.randomUUID());
        JwtUtil wrongJwtUtil = new JwtUtil(wrongKeyPair.getPublic());
        assertThat(wrongJwtUtil.validateToken(token)).isFalse();
    }

    @Test
    void validateToken_withTamperedToken_returnsFalse() {
        JwtUtil jwtUtil = new JwtUtil(keyPair.getPublic(), keyPair.getPrivate());
        String token = jwtUtil.generateToken("tamper", UUID.randomUUID());
        String tampered = token + "x";
        assertThat(jwtUtil.validateToken(tampered)).isFalse();
    }

    @Test
    void validateToken_expiredToken_returnsFalse() {
        JwtUtil jwtUtil = new JwtUtil(keyPair.getPublic(), keyPair.getPrivate(), -10L);
        String token = jwtUtil.generateToken("expired", UUID.randomUUID());
        assertThat(jwtUtil.validateToken(token)).isFalse();
    }

    @Test
    void validateToken_validToken_returnsTrue() {
        JwtUtil jwtUtil = new JwtUtil(keyPair.getPublic(), keyPair.getPrivate());
        String token = jwtUtil.generateToken("charlie", UUID.randomUUID());
        assertThat(jwtUtil.validateToken(token)).isTrue();
    }

    @Test
    void generateToken_withoutPrivateKey_throws() {
        JwtUtil jwtUtil = new JwtUtil(keyPair.getPublic());
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> jwtUtil.generateToken("nokey", id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Private key is not set");
    }

    @Test
    void getRoleFromToken_withRole_returnsRole() {
        JwtUtil jwtUtil = new JwtUtil(keyPair.getPublic(), keyPair.getPrivate());
        String token = jwtUtil.generateToken("admin_user", UUID.randomUUID(), "ADMIN");
        assertThat(jwtUtil.getRoleFromToken(token)).isPresent().contains("ADMIN");
    }

    @Test
    void getRoleFromToken_withUserRole_returnsRole() {
        JwtUtil jwtUtil = new JwtUtil(keyPair.getPublic(), keyPair.getPrivate());
        String token = jwtUtil.generateToken("regular_user", UUID.randomUUID(), "USER");
        assertThat(jwtUtil.getRoleFromToken(token)).isPresent().contains("USER");
    }

    @Test
    void getRoleFromToken_withoutRole_returnsEmpty() {
        JwtUtil jwtUtil = new JwtUtil(keyPair.getPublic(), keyPair.getPrivate());
        String token = jwtUtil.generateToken("no_role_user", UUID.randomUUID(), null);
        assertThat(jwtUtil.getRoleFromToken(token)).isEmpty();
    }
}
