package com.tk.eventmanager.dto;

import com.tk.eventmanager.model.EventStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class EventSearchRequest {

    private EventStatus status;
    private Long locationId;
    private String keyword;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime from;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime to;

    private Integer minCapacity;

    // геттеры, сеттеры
    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }
    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public LocalDateTime getFrom() { return from; }
    public void setFrom(LocalDateTime from) { this.from = from; }
    public LocalDateTime getTo() { return to; }
    public void setTo(LocalDateTime to) { this.to = to; }
    public Integer getMinCapacity() { return minCapacity; }
    public void setMinCapacity(Integer minCapacity) { this.minCapacity = minCapacity; }
}