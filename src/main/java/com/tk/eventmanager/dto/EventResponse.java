package com.tk.eventmanager.dto;

import com.tk.eventmanager.model.EventStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventResponse {

    private Long id;
    private String title;
    private String description;
    private int capacity;
    private String status;        // ← теперь из enum
    private BigDecimal price;
    private LocalDateTime eventDate;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;   // ← НОВОЕ
    private LocalDateTime canceledAt;    // ← НОВОЕ
    private String locationName;
    private Long locationId;
    private boolean registrationOpen;    // ← НОВОЕ: удобно для фронтенда
    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }

    public void setPublishedAt(LocalDateTime publishedAt) {
    }

    public void setCanceledAt(LocalDateTime canceledAt) {
    }

    public void setRegistrationOpen(boolean registrationOpen) {
    }
}