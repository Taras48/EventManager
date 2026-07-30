package com.tk.eventmanager.service;

import com.tk.eventmanager.model.Event;
import com.tk.eventmanager.model.Registration;
import com.tk.eventmanager.model.User;
import com.tk.eventmanager.repository.EventRepository;
import com.tk.eventmanager.repository.RegistrationRepository;
import com.tk.eventmanager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public RegistrationService(RegistrationRepository registrationRepository,
                               EventRepository eventRepository,
                               UserRepository userRepository) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public Registration register(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Registration reg = new Registration();
        reg.setEvent(event);
        reg.setUser(user);

        return registrationRepository.save(reg);
    }

    public List<Registration> getRegistrationsForEvent(Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }
}
