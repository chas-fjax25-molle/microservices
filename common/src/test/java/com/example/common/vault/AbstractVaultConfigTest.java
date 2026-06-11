package com.example.common.vault;

import com.example.exception.InvalidVaultException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AbstractVaultConfigTest {

    private static KeyPair keyPair;
    private static String validPublicKeyPem;
    private static String validPrivateKeyPem;

    @BeforeAll
    static void setUp() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        keyPair = generator.generateKeyPair();

        validPublicKeyPem = "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded())
                + "\n-----END PUBLIC KEY-----";

        validPrivateKeyPem = "-----BEGIN PRIVATE KEY-----\n"
                + Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded())
                + "\n-----END PRIVATE KEY-----";
    }

    static class TestVaultConfig extends AbstractVaultConfig {
        TestVaultConfig(String vaultAddr, String vaultToken, String serviceName) {
            super(vaultAddr, vaultToken, serviceName);
        }
    }

    @Nested
    class ResolveVaultToken {

        @Test
        void returnsTokenWhenProvided() {
            var config = new TestVaultConfig("http://vault:8200", "my-token", "test-svc");
            assertThat(config.resolveVaultToken()).isEqualTo("my-token");
        }

        @Test
        void fallsBackToFileWhenTokenIsBlank() {
            try (MockedStatic<Files> files = mockStatic(Files.class)) {
                files.when(() -> Files.exists(any(Path.class))).thenReturn(false);

                var config = new TestVaultConfig("http://vault:8200", "  ", "test-svc");
                assertThatThrownBy(config::resolveVaultToken)
                        .isInstanceOf(InvalidVaultException.class)
                        .hasMessage("Secret file does not exist");
            }
        }

        @Test
        void throwsWhenSecretFileDoesNotExist() {
            try (MockedStatic<Files> files = mockStatic(Files.class)) {
                files.when(() -> Files.exists(any(Path.class))).thenReturn(false);

                var config = new TestVaultConfig("http://vault:8200", null, "test-svc");
                assertThatThrownBy(config::resolveVaultToken)
                        .isInstanceOf(InvalidVaultException.class)
                        .hasMessage("Secret file does not exist");
            }
        }

        @Test
        void readsTokenFromFile() {
            try (MockedStatic<Files> files = mockStatic(Files.class)) {
                files.when(() -> Files.exists(any(Path.class))).thenReturn(true);
                files.when(() -> Files.readString(any(Path.class))).thenReturn("file-token\n");

                var config = new TestVaultConfig("http://vault:8200", null, "test-svc");
                assertThat(config.resolveVaultToken()).isEqualTo("file-token");
            }
        }

        @Test
        void throwsWhenFileReadFails() {
            try (MockedStatic<Files> files = mockStatic(Files.class)) {
                files.when(() -> Files.exists(any(Path.class))).thenReturn(true);
                files.when(() -> Files.readString(any(Path.class))).thenThrow(new IOException("permission denied"));

                var config = new TestVaultConfig("http://vault:8200", null, "test-svc");
                assertThatThrownBy(config::resolveVaultToken)
                        .isInstanceOf(InvalidVaultException.class)
                        .hasMessageContaining("Failed to read secret file");
            }
        }
    }

    @Nested
    class FetchJwtKeys {

        private final TestVaultConfig config = new TestVaultConfig("http://vault:8200", "token", "svc");
        private final String vaultToken = "some-token";

        @Test
        void returnsSecretsOnSuccessfulResponse() {
            var secrets = Map.of("public_key", "abc", "private_key", "def");
            var body = Map.of("data", Map.of("data", secrets));
            var responseEntity = new ResponseEntity<>((Map) body, HttpStatus.OK);

            try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                    (mock, context) -> when(mock.exchange(anyString(), any(HttpMethod.class),
                            any(HttpEntity.class), eq(Map.class))).thenReturn(responseEntity))) {
                assertThat(config.fetchJwtKeys(vaultToken)).isEqualTo(secrets);
            }
        }

        @Test
        void throwsWhenResponseBodyIsNull() {
            var responseEntity = new ResponseEntity<>((Map) null, HttpStatus.OK);

            try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                    (mock, context) -> when(mock.exchange(anyString(), any(HttpMethod.class),
                            any(HttpEntity.class), eq(Map.class))).thenReturn(responseEntity))) {
                assertThatThrownBy(() -> config.fetchJwtKeys(vaultToken))
                        .isInstanceOf(InvalidVaultException.class)
                        .hasMessage("Failed to fetch JWT keys");
            }
        }

        @Test
        void throwsWhenTopLevelDataIsNull() {
            var body = Map.of();
            var responseEntity = new ResponseEntity<>((Map) body, HttpStatus.OK);

            try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                    (mock, context) -> when(mock.exchange(anyString(), any(HttpMethod.class),
                            any(HttpEntity.class), eq(Map.class))).thenReturn(responseEntity))) {
                assertThatThrownBy(() -> config.fetchJwtKeys(vaultToken))
                        .isInstanceOf(InvalidVaultException.class)
                        .hasMessage("Failed to fetch JWT keys");
            }
        }

        @Test
        void throwsWhenNestedSecretsIsNull() {
            var body = Map.of("data", Map.of());
            var responseEntity = new ResponseEntity<>((Map) body, HttpStatus.OK);

            try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                    (mock, context) -> when(mock.exchange(anyString(), any(HttpMethod.class),
                            any(HttpEntity.class), eq(Map.class))).thenReturn(responseEntity))) {
                assertThatThrownBy(() -> config.fetchJwtKeys(vaultToken))
                        .isInstanceOf(InvalidVaultException.class)
                        .hasMessage("Failed to fetch JWT keys");
            }
        }
    }

    @Nested
    class ParseKeys {

        @Test
        void parseValidPublicKey() throws Exception {
            assertThat(AbstractVaultConfig.parsePublicKey(validPublicKeyPem))
                    .isEqualTo(keyPair.getPublic());
        }

        @Test
        void parseValidPrivateKey() throws Exception {
            assertThat(AbstractVaultConfig.parsePrivateKey(validPrivateKeyPem))
                    .isEqualTo(keyPair.getPrivate());
        }

        @Test
        void parseInvalidPublicKeyThrows() {
            assertThatThrownBy(() -> AbstractVaultConfig.parsePublicKey("garbage"))
                    .isInstanceOf(Exception.class);
        }

        @Test
        void parseInvalidPrivateKeyThrows() {
            assertThatThrownBy(() -> AbstractVaultConfig.parsePrivateKey("garbage"))
                    .isInstanceOf(Exception.class);
        }
    }
}
