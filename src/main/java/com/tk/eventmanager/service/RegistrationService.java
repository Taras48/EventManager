package com.tk.eventmanager.service;

import com.tk.eventmanager.exception.BadRequestException;
import com.tk.eventmanager.exception.DuplicateException;
import com.tk.eventmanager.exception.ResourceNotFoundException;
import com.tk.eventmanager.model.*;
import com.tk.eventmanager.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        // === БИЗНЕС-ПРАВИЛА ===

        // 1. Регистрация открыта только на PUBLISHED события
        if (!event.isRegistrationOpen()) {
            throw new BadRequestException(
                    "Registration is not open for event '" + event.getTitle() +
                            "' (status: " + event.getStatus() + ", capacity: " + event.getCapacity() + ")");
        }

        // 2. Нельзя зарегистрироваться дважды
        if (registrationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new DuplicateException(
                    "User " + userId + " is already registered for event " + eventId);
        }

        // 3. Уменьшаем количество мест
        event.setCapacity(event.getCapacity() - 1);
        // dirty checking → UPDATE

        // 4. Создаём регистрацию
        Registration reg = new Registration();
        reg.setEvent(event);
        reg.setUser(user);
        reg.setStatus(RegistrationStatus.CONFIRMED);

        return registrationRepository.save(reg);
    }

    @Transactional
    public void cancelRegistration(Long registrationId) {
        Registration reg = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration", registrationId));

        if (reg.getStatus() == RegistrationStatus.CANCELED) {
            throw new BadRequestException("Registration is already canceled");
        }

        // Отменяем
        reg.setStatus(RegistrationStatus.CANCELED);

        // Возвращаем место (только если событие ещё актуально)
        Event event = reg.getEvent();
        if (event.getStatus() == EventStatus.PUBLISHED) {
            event.setCapacity(event.getCapacity() + 1);
        }
    }

    @Transactional(readOnly = true)
    public Page<Registration> getRegistrationsForEvent(Long eventId, Pageable pageable) {
        // Проверяем, что событие существует
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event", eventId);
        }
        return registrationRepository.findByEventId(eventId, pageable);
    }
}