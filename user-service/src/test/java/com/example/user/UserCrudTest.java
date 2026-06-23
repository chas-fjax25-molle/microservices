package com.example.user;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.HttpHeaders;
import com.example.common.security.JwtUtil;
import org.springframework.http.MediaType;
import com.jayway.jsonpath.JsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;


@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, statements = "DELETE FROM USERS")
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserCrudTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        when(jwtUtil.validateToken("user-token")).thenReturn(true);
        when(jwtUtil.getIdFromToken("user-token")).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        when(jwtUtil.getRoleFromToken("user-token")).thenReturn(Optional.of("USER"));

        when(jwtUtil.validateToken("admin-token")).thenReturn(true);
        when(jwtUtil.getIdFromToken("admin-token")).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        when(jwtUtil.getRoleFromToken("admin-token")).thenReturn(Optional.of("ADMIN"));
    }

    @Test
    void registerUserShouldReturnCreated() throws Exception {
        mockMvc.perform(post("/api/user-service/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"username\": \"testuser\", \"email\": \"testuser@example.com\", \"password\": \"password123\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void registerUserShouldReturnBadRequestWhenUsernameIsBlank() throws Exception {
        mockMvc.perform(post("/api/user-service/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"\", \"email\": \"testuser@example.com\", \"password\": \"password123\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserByIdShouldReturnOk() throws Exception {
        String registerResponse = mockMvc.perform(post("/api/user-service/users/register")
                .header(HttpHeaders.AUTHORIZATION, "Bearer user-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"username\": \"testuser2\", \"email\": \"testuser2@example.com\", \"password\": \"password123\"}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String userId = JsonPath.read(registerResponse, "$.id");

        mockMvc.perform(get("/api/user-service/users/" + userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer admin-token"))
                .andExpect(status().isOk());

    }
    @Test
    void getUserByIdShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/user-service/users/00000000-0000-0000-0000-000000000000")
                .header(HttpHeaders.AUTHORIZATION, "Bearer admin-token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUserShouldReturnOkWithPatch() throws Exception {
        String registerResponse = mockMvc.perform(post("/api/user-service/users/register")
                .header(HttpHeaders.AUTHORIZATION, "Bearer user-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"username\": \"testuser3\", \"email\": \"testuser3@example.com\", \"password\": \"password123\"}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String userId = JsonPath.read(registerResponse, "$.id");

        mockMvc.perform(patch("/api/user-service/users/" + userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
            {
              "newUsername": "updateduser",
              "newEmail": "updateduser@example.com",
              "newPassword": "newpassword123"
            }
            """))
    .andExpect(status().isOk());

    }
    @Test
    void updateUserShouldReturnNotFoundWithPatch() throws Exception {
        mockMvc.perform(patch("/api/user-service/users/00000000-0000-0000-0000-000000000000")
                .header(HttpHeaders.AUTHORIZATION, "Bearer admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"username\": \"updateduser\", \"email\": \"updateduser@example.com\", \"password\": \"newpassword123\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUserShouldReturnNoContent() throws Exception {
        String registerResponse = mockMvc.perform(post("/api/user-service/users/register")
                .header(HttpHeaders.AUTHORIZATION, "Bearer admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"username\": \"testuser4\", \"email\": \"testuser4@example.com\", \"password\": \"password123\"}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String userId = JsonPath.read(registerResponse, "$.id");

        mockMvc.perform(delete("/api/user-service/users/" + userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer admin-token"))
    .andExpect(status().isNoContent());

    }
    @Test
    void deleteUserShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/user-service/users/00000000-0000-0000-0000-000000000000")
                .header(HttpHeaders.AUTHORIZATION, "Bearer admin-token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserShouldBeForbiddenForUserRole() throws Exception {
        String response = mockMvc.perform(post("/api/user-service/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"usera\", \"email\": \"usera@test.com\", \"password\": \"password123\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String userId = JsonPath.read(response, "$.id");

        mockMvc.perform(get("/api/user-service/users/" + userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer user-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUserShouldBeForbiddenForUserRole() throws Exception {
        String response = mockMvc.perform(post("/api/user-service/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"userb\", \"email\": \"userb@test.com\", \"password\": \"password123\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String userId = JsonPath.read(response, "$.id");

        mockMvc.perform(patch("/api/user-service/users/" + userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer user-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"newEmail\": \"hacker@test.com\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUserShouldBeForbiddenForUserRole() throws Exception {
        String response = mockMvc.perform(post("/api/user-service/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"userc\", \"email\": \"userc@test.com\", \"password\": \"password123\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String userId = JsonPath.read(response, "$.id");

        mockMvc.perform(delete("/api/user-service/users/" + userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer user-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMeShouldReturnOwnData() throws Exception {
        String response = mockMvc.perform(post("/api/user-service/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"selfuser\", \"email\": \"selfuser@test.com\", \"password\": \"password123\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String userId = JsonPath.read(response, "$.id");

        when(jwtUtil.getIdFromToken("user-token")).thenReturn(UUID.fromString(userId));

        mockMvc.perform(get("/api/user-service/users/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer user-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));
    }

    @Test
    void updateMeShouldUpdateOwnData() throws Exception {
        String response = mockMvc.perform(post("/api/user-service/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"selfupdater\", \"email\": \"selfupdater@test.com\", \"password\": \"password123\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String userId = JsonPath.read(response, "$.id");

        when(jwtUtil.getIdFromToken("user-token")).thenReturn(UUID.fromString(userId));

        mockMvc.perform(patch("/api/user-service/users/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer user-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"newEmail\": \"updatedself@test.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updatedself@test.com"));
    }
}
