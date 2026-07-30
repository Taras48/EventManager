package com.tk.eventmanager.service;

import com.tk.eventmanager.dto.EventCreateRequest;
import com.tk.eventmanager.model.Event;
import com.tk.eventmanager.model.Location;
import com.tk.eventmanager.repository.EventRepository;
import com.tk.eventmanager.repository.LocationRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;

    public EventService(EventRepository eventRepository,
                        LocationRepository locationRepository) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
    }
    @Transactional
    public Event createEvent(String title, String description,
                             int capacity, Long locationId) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setCapacity(capacity);
        event.setStatus("DRAFT");

        if (locationId != null) {
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new RuntimeException("Location not found: " + locationId));
            event.setLocation(location);
        }

        return eventRepository.save(event);
    }
    @Transactional(readOnly = true)
    public Event getEventWithLocation(Long id) {
        return eventRepository.findByIdWithLocation(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Transactional
    public Event updateEvent(Long id, EventCreateRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCapacity(request.getCapacity());
        event.setPrice(request.getPrice());
        event.setEventDate(request.getEventDate());

        if (request.getLocationId() != null) {
            Location location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            event.setLocation(location);
        } else {
            event.setLocation(null);
        }

        // dirty checking → UPDATE при COMMIT
        return event;
    }

    public Optional<Event> getEventsByStatus(String status) {
        return Optional.of(new Event());
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}