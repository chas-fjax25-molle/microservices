package com.example.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.user.model.User;
import com.jayway.jsonpath.JsonPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserServiceApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void registerUserShouldReturnCreated() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password123");

        mockMvc.perform(post("/api/user-service/users/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"username\": \"testuser\", \"email\": \"testuser@example.com\", \"password\": \"password123\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void registerUserShouldReturnBadRequestWhenUsernameIsBlank() throws Exception {
        User user = new User();
        user.setUsername("");
        user.setEmail("testuser@example.com");
        user.setPassword("password123");

        mockMvc.perform(post("/api/user-service/users/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"\", \"email\": \"testuser@example.com\", \"password\": \"password123\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserByIdShouldReturnOk() throws Exception {
        String registerResponse = mockMvc.perform(post("/api/user-service/users/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"username\": \"testuser2\", \"email\": \"testuser2@example.com\", \"password\": \"password123\"}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String userId = JsonPath.read(registerResponse, "$.id");

        mockMvc.perform(get("/api/user-service/users/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    void getUserByIdShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/user-service/users/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUserShouldReturnOkWithPatch() throws Exception {
        String registerResponse = mockMvc.perform(post("/api/user-service/users/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"username\": \"testuser3\", \"email\": \"testuser3@example.com\", \"password\": \"password123\"}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String userId = JsonPath.read(registerResponse, "$.id");

        mockMvc.perform(patch("/api/user-service/users/" + userId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"username\": \"updateduser\", \"email\": \"updateduser@example.com\", \"password\": \"newpassword123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateUserShouldReturnNotFoundWithPatch() throws Exception {
        mockMvc.perform(patch("/api/user-service/users/00000000-0000-0000-0000-000000000000")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"username\": \"updateduser\", \"email\": \"updateduser@example.com\", \"password\": \"newpassword123\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUserShouldReturnNoContent() throws Exception {
        User user = new User();
        user.setUsername("testuser4");
        user.setEmail("testuser4@example.com");
        user.setPassword("password123");

        String registerResponse = mockMvc.perform(post("/api/user-service/users/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"username\": \"testuser4\", \"email\": \"testuser4@example.com\", \"password\": \"password123\"}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String userId = JsonPath.read(registerResponse, "$.id");

        mockMvc.perform(delete("/api/user-service/users/" + userId)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUserShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/user-service/users/00000000-0000-0000-0000-000000000000")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }
}