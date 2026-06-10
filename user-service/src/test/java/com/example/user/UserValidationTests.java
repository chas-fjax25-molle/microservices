package com.example.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(statements = """
        DELETE FROM USERS;
        INSERT INTO USERS (ID, USERNAME, EMAIL, PASSWORD) VALUES
            ('00000000-0000-0000-0000-000000000000', 'test', 'test@test.com', '1234')
            """)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserValidationTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnUserInformationOnValidCredentials() throws Exception {
        mockMvc.perform(
                post("/api/user-service/users/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"test\",\"password\":\"1234\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedOnInvalidPassword() throws Exception {
        mockMvc.perform(
                post("/api/user-service/users/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"test\",\"password\":\"4321\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedOnInvalidUsername() throws Exception {
        mockMvc.perform(
                post("/api/user-service/users/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"fake\",\"password\":\"4321\"}"))
                .andExpect(status().isUnauthorized());
    }
}
