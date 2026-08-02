package com.tk.eventmanager.mapper;

import com.tk.eventmanager.dto.RegistrationResponse;
import com.tk.eventmanager.model.Registration;
import org.springframework.stereotype.Component;

@Component
public class RegistrationMapper {

    public RegistrationResponse toResponse(Registration reg) {
        RegistrationResponse dto = new RegistrationResponse();
        dto.setId(reg.getId());
        dto.setEventId(reg.getEvent().getId());
        dto.setEventTitle(reg.getEvent().getTitle());
        dto.setUserId(reg.getUser().getId());
        dto.setUserName(reg.getUser().getName());
        dto.setStatus(reg.getStatus().name());
        dto.setRegisteredAt(reg.getRegisteredAt());
        return dto;
    }
}