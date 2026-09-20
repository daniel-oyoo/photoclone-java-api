package com.daniel.photoclone;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PhotocloneIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void photoEndpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/photos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registrationIsPublicAndRegisteredUserCanReadPhotos() throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "integration-user",
                                  "password": "strong-password",
                                  "email": "integration@example.com",
                                  "fullName": "Integration User"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/photos").with(httpBasic("integration-user", "strong-password")))
                .andExpect(status().isOk());
    }
}