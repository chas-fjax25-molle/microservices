package com.example.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.UUID;

/**
 * Utility class to parse, validate, and generate JWT.
 */
public class JwtUtil {
    private static final long DEFAULT_EXPIRATION_MS = 3_600_000L;

    private final PublicKey publicKey;
    private final PrivateKey privateKey;
    private final long expirationMs;

    public JwtUtil(PublicKey publicKey) {
        this(publicKey, null, DEFAULT_EXPIRATION_MS);
    }

    public JwtUtil(PublicKey publicKey, PrivateKey privateKey) {
        this(publicKey, privateKey, DEFAULT_EXPIRATION_MS);
    }

    public JwtUtil(PublicKey publicKey, PrivateKey privateKey, long expirationMs) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.expirationMs = expirationMs;
    }

    /**
     * Validates that a JWT token is correctly signed ant not expired.
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception _) {
            return false;
        }
    }

    /**
     * Parses a JWT token and returns the username.
     *
     * @param token the JWT token to parse
     * @return the parsed username
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Generates a JWT token for the given username and ID.
     *
     * @param username the username to include in the token
     * @param id       the ID of the user to include in the token
     * @return the generated JWT token
     * 
     * @throws IllegalStateException if the private key is not set
     */
    public String generateToken(String username, UUID id) {
        if (privateKey == null) {
            throw new IllegalStateException("Private key is not set, cannot generate token");
        }

        Date name = new Date();

        return Jwts.builder()
                .subject(username)
                .claim("id", id.toString())
                .issuedAt(name)
                .expiration(new Date(name.getTime() + expirationMs))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
