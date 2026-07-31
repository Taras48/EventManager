package com.tk.eventmanager.service;

import com.tk.eventmanager.dto.AuthResponse;
import com.tk.eventmanager.dto.LoginRequest;
import com.tk.eventmanager.dto.RegisterRequest;
import com.tk.eventmanager.exception.DuplicateException;
import com.tk.eventmanager.model.User;
import com.tk.eventmanager.repository.UserRepository;
import com.tk.eventmanager.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Проверка: email уже занят?
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateException("Email already exists: " + request.getEmail());
        }

        // Создаём пользователя
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));  // ← ХЭШ!
        user.setRole("USER");  // по умолчанию

        userRepository.save(user);

        // Генерируем токен
        String token = jwtService.generateToken(user.getEmail(), user.getRole());

        return new AuthResponse(token, user.getEmail(), user.getName(),
                user.getRole(), expirationMs);
    }

    public AuthResponse login(LoginRequest request) {
        // Аутентификация: Spring Security проверяет email + пароль
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Если дошли сюда — пароль верный
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user.getEmail(), user.getRole());

        return new AuthResponse(token, user.getEmail(), user.getName(),
                user.getRole(), expirationMs);
    }
}