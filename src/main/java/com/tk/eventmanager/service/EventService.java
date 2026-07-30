package com.tk.eventmanager.service;

import com.tk.eventmanager.model.Event;
import com.tk.eventmanager.model.Location;
import com.tk.eventmanager.repository.EventRepository;
import com.tk.eventmanager.repository.LocationRepository;
import org.springframework.stereotype.Service;

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

    public Event getEventWithLocation(Long id) {
        return eventRepository.findByIdWithLocation(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));
    }
}