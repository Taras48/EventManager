package com.tk.eventmanager.service;

import com.tk.eventmanager.model.Event;
import com.tk.eventmanager.repository.InMemoryEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final InMemoryEventRepository repository;

    public EventService(InMemoryEventRepository repository) {
        this.repository = repository;
        System.out.println("[SERVICE] Мне внедрили репозиторий: " + repository);
    }

    public Event createEvent(String title) {
        Event event = new Event();
        event.setTitle(title);
        return repository.save(event);
    }

    public List<Event> getAllEvents() {
        return repository.findAll();
    }

    public Optional<Event> getEvent(Long id) {
        return repository.findById(id);
    }
}
