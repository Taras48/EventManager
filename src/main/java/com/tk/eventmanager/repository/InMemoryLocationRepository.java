package com.tk.eventmanager.repository;

import com.tk.eventmanager.model.Location;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Profile("dev")
@Repository
public class InMemoryLocationRepository implements LocationRepository{

    private final ConcurrentHashMap<Long, Location> storage = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    @PostConstruct
    public void init(){
        System.out.println("PostConstruct - InMemoryLocationRepository");
    }
    @PreDestroy
    public void destroy(){
        System.out.println("destroy - InMemoryLocationRepository");
    }

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

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
}
