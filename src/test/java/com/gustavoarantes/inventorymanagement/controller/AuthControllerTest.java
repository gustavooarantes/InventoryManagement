package com.gustavoarantes.inventorymanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gustavoarantes.inventorymanagement.dto.AuthRequest;
import com.gustavoarantes.inventorymanagement.dto.AuthResponse;
import com.gustavoarantes.inventorymanagement.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("Test User", "test.user@example.com", "password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User registered successfully!"));
    }

    @Test
    void shouldLoginSuccessfullyAndReturnJwt() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("Login Test", "login@example.com", "password123");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        AuthRequest authRequest = new AuthRequest("login@example.com", "password123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").isNotEmpty());
    }

    @Test
    void shouldDenyAccessToAdminEndpointForUserRole() throws Exception {
        RegisterRequest userRequest = new RegisterRequest("Regular User", "user@example.com", "password123");
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRequest)));

        AuthRequest authRequest = new AuthRequest("user@example.com", "password123");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(authRequest)))
                .andReturn();
        String jsonResponse = loginResult.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(jsonResponse, AuthResponse.class);
        String userToken = authResponse.jwt();

        String productJson = "{\"name\":\"Admin Product\",\"description\":\"Test\",\"price\":10.0,\"stockQuantity\":10,\"criticalStockLimit\":2}";
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isForbidden());
    }
}