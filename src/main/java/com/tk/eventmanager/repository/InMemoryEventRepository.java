package com.tk.eventmanager.repository;

import com.tk.eventmanager.model.Event;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryEventRepository {

    private final Map<Long, Event> storage = new HashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    public Event save(Event event) {
        if (event.getId() == null) {
            event.setId(counter.getAndIncrement());
        }
        storage.put(event.getId(), event);
        System.out.println("[REPO] Сохранено: " + event);
        return event;
    }

    public Optional<Event> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Event> findAll() {
        return new ArrayList<>(storage.values());
    }
}
