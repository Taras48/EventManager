package com.tk.eventmanager.service;

import com.tk.eventmanager.model.Event;
import com.tk.eventmanager.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository repository;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public Event createEvent(String title, String description, int capacity) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setCapacity(capacity);
        event.setStatus("DRAFT");
        return repository.save(event);  // ← INSERT в БД
    }

    public Optional<Event> getEvent(Long id) {
        return repository.findById(id);  // ← SELECT по ID
    }

    public List<Event> getAllEvents() {
        return repository.findAll();  // ← SELECT *
    }

    public void deleteEvent(Long id) {
        repository.deleteById(id);  // ← DELETE
    }

    public List<Event> getEventsByStatus(String status) {
        return repository.findByStatus(status);  // ← кастомный запрос
    }
}