package com.tk.eventmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tk.eventmanager.dto.EventCreateRequest;
import com.tk.eventmanager.model.User;
import com.tk.eventmanager.repository.UserRepository;
import com.tk.eventmanager.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        // Создаём USER
        User user = new User();
        user.setName("User");
        user.setEmail("user@test.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole("USER");
        userRepository.save(user);

        // Создаём ADMIN
        User admin = new User();
        admin.setName("Admin");
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setRole("ADMIN");
        userRepository.save(admin);

        // Генерируем токены
        userToken = jwtService.generateAccessToken("user@test.com", "USER");
        adminToken = jwtService.generateAccessToken("admin@test.com", "ADMIN");
    }

    @Test
    void getEvents_withoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getEvents_withUserToken_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/events")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void createEvent_withUserToken_shouldReturn403() throws Exception {
        EventCreateRequest request = new EventCreateRequest();
        request.setTitle("Meetup");
        request.setCapacity(50);

        mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());  // USER не может создавать
    }

    @Test
    void createEvent_withAdminToken_shouldReturn201() throws Exception {
        EventCreateRequest request = new EventCreateRequest();
        request.setTitle("Java Meetup");
        request.setCapacity(50);

        mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Java Meetup"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void getEvent_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/events/999")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Event not found: 999"));
    }

    @Test
    void deleteEvent_withUserToken_shouldReturn403() throws Exception {
        // Сначала создаём событие как ADMIN
        EventCreateRequest request = new EventCreateRequest();
        request.setTitle("To Delete");
        request.setCapacity(10);

        String response = mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        Long eventId = objectMapper.readTree(response).get("id").asLong();

        // USER пытается удалить
        mockMvc.perform(delete("/api/events/" + eventId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}