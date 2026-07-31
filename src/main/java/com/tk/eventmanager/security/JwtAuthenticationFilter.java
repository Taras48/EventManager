package com.tk.eventmanager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Достаём заголовок Authorization
        String authHeader = request.getHeader("Authorization");

        // 2. Нет заголовка или не "Bearer ..." → пропускаем дальше (без аутентификации)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Извлекаем токен
        String token = authHeader.substring(7);  // убираем "Bearer "

        try {
            // 4. Извлекаем email из токена
            String email = jwtService.extractEmail(token);

            // 5. Если email есть и пользователь ещё не аутентифицирован
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6. Загружаем пользователя из БД
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // 7. Валидируем токен
                if (jwtService.isTokenValid(token)) {

                    // 8. Создаём объект аутентификации
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()  // роли
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    // 9. Устанавливаем в SecurityContext
                    // Теперь Spring Security знает: "Этот запрос от user@email.com с ролью ROLE_USER"
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Токен невалидный → просто не аутентифицируем
            // Запрос пойдёт дальше как анонимный
        }

        filterChain.doFilter(request, response);
    }
}