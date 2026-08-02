package com.tk.eventmanager.service;

import com.tk.eventmanager.dto.EventCreateRequest;
import com.tk.eventmanager.dto.EventSearchRequest;
import com.tk.eventmanager.exception.BadRequestException;
import com.tk.eventmanager.exception.ResourceNotFoundException;
import com.tk.eventmanager.model.Event;
import com.tk.eventmanager.model.EventStatus;
import com.tk.eventmanager.model.Location;
import com.tk.eventmanager.repository.EventRepository;
import com.tk.eventmanager.repository.LocationRepository;
import com.tk.eventmanager.specification.EventSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;

    public EventService(EventRepository eventRepository, LocationRepository locationRepository) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
    }

    @Transactional
    public Event createEvent(EventCreateRequest request) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCapacity(request.getCapacity());
        event.setPrice(request.getPrice());
        event.setEventDate(request.getEventDate());
        // status = DRAFT (по умолчанию в Entity)

        if (request.getLocationId() != null) {
            Location location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location", request.getLocationId()));
            event.setLocation(location);
        }

        return eventRepository.save(event);
    }

    // === ПЕРЕХОДЫ СТАТУСОВ ===

    @Transactional
    public Event publishEvent(Long id) {
        Event event = getEventOrThrow(id);
        event.publish();  // ← проверка + смена статуса внутри модели
        // dirty checking → UPDATE при COMMIT
        return event;
    }

    @Transactional
    public Event cancelEvent(Long id) {
        Event event = getEventOrThrow(id);
        event.cancel();
        return event;
    }

    @Transactional
    public Event completeEvent(Long id) {
        Event event = getEventOrThrow(id);
        event.complete();
        return event;
    }

    @Transactional
    public Event archiveEvent(Long id) {
        Event event = getEventOrThrow(id);
        event.archive();
        return event;
    }

    // === ЧТЕНИЕ ===

    @Transactional(readOnly = true)
    public Event getEvent(Long id) {
        return getEventOrThrow(id);
    }

    @Transactional(readOnly = true)
    public Event getEventWithLocation(Long id) {
        return eventRepository.findByIdWithLocation(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));
    }

    @Transactional(readOnly = true)
    public Page<Event> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Event> getEventsByStatus(EventStatus status, Pageable pageable) {
        return eventRepository.findByStatus(status.name(), pageable);
    }

    // === ОБНОВЛЕНИЕ (только DRAFT) ===

    @Transactional
    public Event updateEvent(Long id, EventCreateRequest request) {
        Event event = getEventOrThrow(id);

        // Редактировать можно ТОЛЬКО черновик
        if (event.getStatus() != EventStatus.DRAFT) {
            throw new BadRequestException(
                    "Cannot edit event in status " + event.getStatus() + ". Only DRAFT events can be edited.");
        }

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCapacity(request.getCapacity());
        event.setPrice(request.getPrice());
        event.setEventDate(request.getEventDate());

        if (request.getLocationId() != null) {
            Location location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location", request.getLocationId()));
            event.setLocation(location);
        } else {
            event.setLocation(null);
        }

        return event;
    }

    // === УДАЛЕНИЕ (только DRAFT) ===

    @Transactional
    public void deleteEvent(Long id) {
        Event event = getEventOrThrow(id);

        if (event.getStatus() != EventStatus.DRAFT) {
            throw new BadRequestException(
                    "Cannot delete event in status " + event.getStatus() + ". Only DRAFT events can be deleted.");
        }

        eventRepository.delete(event);
    }

    @Transactional(readOnly = true)
    public Page<Event> searchEvents(EventSearchRequest search, Pageable pageable) {
        Specification<Event> spec = EventSpecifications.withFilters(
                search.getStatus(),
                search.getLocationId(),
                search.getKeyword(),
                search.getFrom(),
                search.getTo(),
                search.getMinCapacity()
        );

        return eventRepository.findAll(spec, pageable);
    }

    // === HELPER ===

    private Event getEventOrThrow(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));
    }
}