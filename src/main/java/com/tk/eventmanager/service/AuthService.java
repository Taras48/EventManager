package com.tk.eventmanager.service;

import com.tk.eventmanager.dto.AuthResponse;
import com.tk.eventmanager.dto.LoginRequest;
import com.tk.eventmanager.dto.RegisterRequest;
import com.tk.eventmanager.dto.RefreshRequest;
import com.tk.eventmanager.exception.BadRequestException;
import com.tk.eventmanager.exception.DuplicateException;
import com.tk.eventmanager.exception.ResourceNotFoundException;
import com.tk.eventmanager.model.RefreshToken;
import com.tk.eventmanager.model.User;
import com.tk.eventmanager.repository.RefreshTokenRepository;
import com.tk.eventmanager.repository.UserRepository;
import com.tk.eventmanager.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // === Регистрация ===
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateException("Email already exists: " + request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    // === Логин ===
    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", 0L));

        return buildAuthResponse(user);
    }

    // === Refresh ===
    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        // 1. Находим refresh-токен в БД
        RefreshToken storedToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        // 2. Проверяем: не отозван? не истёк?
        if (storedToken.isRevoked()) {
            throw new BadRequestException("Refresh token has been revoked");
        }
        if (storedToken.isExpired()) {
            storedToken.setRevoked(true);
            throw new BadRequestException("Refresh token expired. Please login again.");
        }

        // 3. Отзываем старый refresh (rotation!)
        storedToken.setRevoked(true);

        // 4. Генерируем новую пару
        User user = storedToken.getUser();
        return buildAuthResponse(user);
    }

    // === Logout ===
    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    // === Общий метод: генерация пары токенов ===
    private AuthResponse buildAuthResponse(User user) {
        // Access
        String accessToken = jwtService.generateAccessToken(
                user.getEmail(), user.getRole());

        // Refresh
        String refreshTokenValue = jwtService.generateRefreshToken();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(
                LocalDateTime.now().plusSeconds(jwtService.getRefreshExpirationMs() / 1000));
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(
                accessToken,
                refreshTokenValue,
                user.getEmail(),
                user.getName(),
                user.getRole(),
                jwtService.getRefreshExpirationMs()
        );
    }
}