package com.example.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.common.security.JwtUtil;
import com.example.common.vault.AbstractVaultConfig;

@Configuration
public class JwtConfig extends AbstractVaultConfig {

    public JwtConfig(
        @Value("${vault.addr}") String vaultAddr,
        @Value("${vault.token:}") String vaultToken,
        @Value("${spring.application.name}") String serviceName) {
        super(vaultAddr, vaultToken, serviceName);
    }

    @Bean
    public JwtUtil jwtUtil() throws Exception {
        String token = resolveVaultToken();
        
        var keys = fetchJwtKeys(token);
        
        var publicKey = parsePublicKey(keys.get("public_key"));
        var privateKey = parsePrivateKey(keys.get("private_key")); 
        
        return new JwtUtil(publicKey, privateKey);
        
    }
    
}
