package com.example.user.config;

import java.security.PublicKey;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.common.security.JwtUtil;
import com.example.common.vault.AbstractVaultConfig;

@Configuration
public class VaultJwtConfig extends AbstractVaultConfig {

    public VaultJwtConfig(
        @Value("${vault.addr:http://localhost:8202/}") String vaultAddr,
        @Value("${vault.token:}") String vaultToken,
        @Value("${spring.application.name}") String serviceName) {
            super(vaultAddr.endsWith("/") ? vaultAddr : vaultAddr + "/", vaultToken, serviceName);
        }
    
    @Bean
    JwtUtil jwtUtil() throws Exception{
        String token = resolveVaultToken();
        Map<String, String> keys = fetchJwtKeys(token);

        PublicKey publicKey = parsePublicKey(keys.get("public_key"));

        return new JwtUtil(publicKey);
    }
}
