package com.example.common.vault;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.example.exception.InvalidVaultException;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * Abstract class for Vault configuration. It provides methods for resolving the
 * vault token and reading secrets from Vault.
 */
public abstract class AbstractVaultConfig {
    private static final String SECRETS_FILE_PATH = "/run/secrets/vault-token-";
    private final String vaultAddr;
    private final String vaultToken;
    private final String serviceName;

    protected AbstractVaultConfig(@NotNull String vaultAddr, @Nullable String vaultToken, @NotNull String serviceName) {
        this.vaultAddr = vaultAddr;
        this.vaultToken = vaultToken;
        this.serviceName = serviceName;
    }

    /**
     * Tries to resolve the vault token. If it is not provided in the constructor or
     * as an environment variable, tries to resolve it from a file.
     *
     * @return The vault token
     * @throws InvalidVaultException if the token cannot be resolved
     */
    protected String resolveVaultToken() {
        if (vaultToken != null && !vaultToken.isBlank()) {
            return vaultToken;
        }

        Path secretsFile = Path.of(SECRETS_FILE_PATH + serviceName);
        if (!Files.exists(secretsFile)) {
            throw new InvalidVaultException("Secret file does not exist");
        }

        try {
            return Files.readString(secretsFile).trim();
        } catch (Exception e) {
            throw new InvalidVaultException("Failed to read secret file from " + secretsFile, e);
        }
    }

    /**
     * Tries to read the JWT keys from the vault.
     * 
     * @param token Vault token
     * @return A map of the key names and values
     * @throws InvalidVaultException If the vault call fails or the response is not
     *                               the correct data
     */
    @SuppressWarnings("unchecked") // Prevent LSP complaining of the map casts
    protected Map<String, String> fetchJwtKeys(String token) {
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        var request = new HttpEntity<>(headers);

        String url = vaultAddr + "v1/microservices/data/jwt";
        var response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null)
            throw new InvalidVaultException("Failed to fetch JWT keys");
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        if (data == null)
            throw new InvalidVaultException("Failed to fetch JWT keys");
        Map<String, String> secrets = (Map<String, String>) data.get("data");
        if (secrets == null)
            throw new InvalidVaultException("Failed to fetch JWT keys");

        return secrets;
    }

    /** Parse a PEM-encoded key into a PublicKey. */
    public static PublicKey parsePublicKey(String pem) throws Exception {
        String base64 = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(base64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    /** Parse a PEM-encoded key into a PrivateKey. */
    public static PrivateKey parsePrivateKey(String pem) throws Exception {
        String base64 = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(base64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}
