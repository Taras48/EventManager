package com.tk.eventmanager.repository;

import com.tk.eventmanager.model.Location;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryLocationRepository {

    private final Map<Long, Location> storage = new HashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    public Location save(Location event) {
        if (event.getId() == null) {
            event.setId(counter.getAndIncrement());
        }
        storage.put(event.getId(), event);
        System.out.println("[REPO] Сохранено: " + event);
        return event;
    }

    public Optional<Location> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Location> findAll() {
        return new ArrayList<>(storage.values());
    }
}
