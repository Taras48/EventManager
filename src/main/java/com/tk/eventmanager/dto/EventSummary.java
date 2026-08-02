package com.tk.eventmanager.dto;

import java.time.LocalDateTime;

public interface EventSummary {
    Long getId();
    String getTitle();
    String getStatus();
    LocalDateTime getEventDate();
    int getCapacity();
    String getLocationName();  // ← Hibernate сам сделает JOIN
}