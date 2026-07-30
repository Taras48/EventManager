package com.tk.eventmanager.service;

import com.tk.eventmanager.model.Event;
import com.tk.eventmanager.model.Registration;
import com.tk.eventmanager.model.User;
import com.tk.eventmanager.repository.EventRepository;
import com.tk.eventmanager.repository.RegistrationRepository;
import com.tk.eventmanager.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Registration register(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found: " + eventId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        if (registrationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new RuntimeException("User already registered for this event");
        }

        if (event.getCapacity() <= 0) {
            throw new RuntimeException("No seats available");
        }

        event.setCapacity(event.getCapacity() - 1);
        // save() не нужен — dirty checking

        Registration reg = new Registration();
        reg.setEvent(event);
        reg.setUser(user);
        return registrationRepository.save(reg);
    }

    @Transactional(readOnly = true)
    public List<Registration> getRegistrationsForEvent(Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }

    @Transactional
    public void cancelRegistration(Long registrationId) {
        Registration reg = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        reg.setStatus("CANCELED");
        // dirty checking → UPDATE при COMMIT

        // Возвращаем место
        Event event = reg.getEvent();  // LAZY, но мы в транзакции → ок
        event.setCapacity(event.getCapacity() + 1);
    }
}
