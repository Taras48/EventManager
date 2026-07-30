package com.tk.eventmanager.dto;

import jakarta.validation.constraints.NotNull;

public class RegistrationRequest {

    @NotNull(message = "eventId is required")
    private Long eventId;

    @NotNull(message = "userId is required")
    private Long userId;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}