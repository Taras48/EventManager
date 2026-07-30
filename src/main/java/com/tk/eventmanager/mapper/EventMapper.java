package com.tk.eventmanager.mapper;

import com.tk.eventmanager.dto.EventResponse;
import com.tk.eventmanager.model.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventResponse toResponse(Event event) {
        EventResponse dto = new EventResponse();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setCapacity(event.getCapacity());
        dto.setStatus(event.getStatus());
        dto.setPrice(event.getPrice());
        dto.setEventDate(event.getEventDate());
        dto.setCreatedAt(event.getCreatedAt());

        // LAZY-связь: проверяем, загружена ли
        if (event.getLocation() != null) {
            dto.setLocationId(event.getLocation().getId());
            dto.setLocationName(event.getLocation().getName());
        }

        return dto;
    }
}