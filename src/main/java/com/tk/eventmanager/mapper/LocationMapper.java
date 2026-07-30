package com.tk.eventmanager.mapper;

import com.tk.eventmanager.dto.LocationResponse;
import com.tk.eventmanager.model.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public LocationResponse toResponse(Location location) {
        LocationResponse dto = new LocationResponse();
        dto.setId(location.getId());
        dto.setName(location.getName());
        dto.setAddress(location.getAddress());
        dto.setCapacity(location.getCapacity());
        dto.setCreatedAt(location.getCreatedAt());
        return dto;
    }
}