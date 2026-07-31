package com.tk.eventmanager.dto;

public class AuthResponse {

    private String token;
    private String email;
    private String name;
    private String role;
    private final String tokenType =  "Bearer";
    private long expiresIn;

    public AuthResponse(String token, String email, String name, String role, long expiresIn) {
        this.token = token;
        this.email = email;
        this.name = name;
        this.role = role;
        this.expiresIn = expiresIn;
    }

    // геттеры
    public String getToken() { return token; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public long getExpiresIn() { return expiresIn; }

    public String getTokenType() {
        return tokenType;
    }
}