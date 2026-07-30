package com.tk.eventmanager.service;

import com.tk.eventmanager.exception.BadRequestException;
import com.tk.eventmanager.exception.DuplicateException;
import com.tk.eventmanager.exception.ResourceNotFoundException;
import com.tk.eventmanager.model.Event;
import com.tk.eventmanager.model.Registration;
import com.tk.eventmanager.model.User;
import com.tk.eventmanager.repository.EventRepository;
import com.tk.eventmanager.repository.RegistrationRepository;
import com.tk.eventmanager.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (registrationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new DuplicateException(
                    "User " + userId + " already registered for event " + eventId);
        }

        if (event.getCapacity() <= 0) {
            throw new BadRequestException("No seats available for event: " + eventId);
        }

        event.setCapacity(event.getCapacity() - 1);
        // save() не нужен — dirty checking

        Registration reg = new Registration();
        reg.setEvent(event);
        reg.setUser(user);
        return registrationRepository.save(reg);
    }

    @Transactional(readOnly = true)
    public Page<Registration> getRegistrationsForEvent(Long eventId, Pageable pageable) {
        return registrationRepository.findByEventId(eventId, pageable);
    }

    @Transactional
    public void cancelRegistration(Long registrationId) {
        Registration reg = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration", registrationId));

        reg.setStatus("CANCELED");
        // dirty checking → UPDATE при COMMIT

        // Возвращаем место
        Event event = reg.getEvent();  // LAZY, но мы в транзакции → ок
        event.setCapacity(event.getCapacity() + 1);
    }
}
