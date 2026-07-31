package com.tk.eventmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tk.eventmanager.dto.LoginRequest;
import com.tk.eventmanager.dto.RegisterRequest;
import com.tk.eventmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest          // ← поднимает ВЕСЬ контекст (сервер, БД, security)
@ActiveProfiles("test")  // ← использует application-test.properties
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    void register_shouldReturn201AndTokens() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Тарас");
        request.setEmail("taras@test.com");
        request.setPassword("secret123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.email").value("taras@test.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void register_duplicateEmail_shouldReturn409() throws Exception {
        // Регистрируем первого
        RegisterRequest request = new RegisterRequest();
        request.setName("Тарас");
        request.setEmail("taras@test.com");
        request.setPassword("secret123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Пробуем ещё раз с тем же email
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already exists: taras@test.com"));
    }

    @Test
    void register_invalidData_shouldReturn400() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("");       // ← @NotBlank
        request.setEmail("bad");   // ← @Email
        request.setPassword("12"); // ← @Size(min=6)

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isNotEmpty());
    }

    @Test
    void login_validCredentials_shouldReturn200() throws Exception {
        // Сначала регистрируем
        RegisterRequest reg = new RegisterRequest();
        reg.setName("Тарас");
        reg.setEmail("taras@test.com");
        reg.setPassword("secret123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        // Теперь логинимся
        LoginRequest login = new LoginRequest();
        login.setEmail("taras@test.com");
        login.setPassword("secret123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void login_wrongPassword_shouldReturn401() throws Exception {
        // Регистрируем
        RegisterRequest reg = new RegisterRequest();
        reg.setName("Тарас");
        reg.setEmail("taras@test.com");
        reg.setPassword("secret123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)));

        // Неверный пароль
        LoginRequest login = new LoginRequest();
        login.setEmail("taras@test.com");
        login.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }
}