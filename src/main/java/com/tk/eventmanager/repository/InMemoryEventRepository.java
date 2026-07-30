package com.tk.eventmanager.repository;

import com.tk.eventmanager.model.Event;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Profile("dev")
@Repository
public class InMemoryEventRepository implements EventRepository {

    private final ConcurrentHashMap<Long, Event> storage = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    @PostConstruct
    public void init(){
        System.out.println("PostConstruct - InMemoryEventRepository");
    }
    @PreDestroy
    public void destroy(){
        System.out.println("PostConstruct - InMemoryEventRepository");
    }

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

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
}
