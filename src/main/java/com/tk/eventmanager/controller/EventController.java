package com.tk.eventmanager.controller;

import com.tk.eventmanager.dto.EventCreateRequest;
import com.tk.eventmanager.dto.EventResponse;
import com.tk.eventmanager.mapper.EventMapper;
import com.tk.eventmanager.model.Event;
import com.tk.eventmanager.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    // GET /api/events → список всех событий
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        List<EventResponse> events = eventService.getAllEvents().stream()
                .map(eventMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(events);  // 200 OK
    }

    // GET /api/events/5 → одно событие
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable Long id) {
        Event event = eventService.getEventWithLocation(id);
        return ResponseEntity.ok(eventMapper.toResponse(event));
    }

    // POST /api/events → создать событие
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody EventCreateRequest request) {

        Event event = eventService.createEvent(
                request.getTitle(),
                request.getDescription(),
                request.getCapacity(),
                request.getLocationId()
        );

        EventResponse response = eventMapper.toResponse(event);
        return ResponseEntity
                .status(HttpStatus.CREATED)   // 201 Created
                .body(response);
    }

    // PUT /api/events/5 → обновить событие
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventCreateRequest request) {

        Event event = eventService.updateEvent(id, request);
        return ResponseEntity.ok(eventMapper.toResponse(event));
    }

    // DELETE /api/events/5 → удалить
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }

    // GET /api/events?status=DRAFT → фильтрация
    @GetMapping(params = "status")
    public ResponseEntity<List<EventResponse>> getEventsByStatus(
            @RequestParam String status) {
        List<EventResponse> events = eventService.getEventsByStatus(status).stream()
                .map(eventMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(events);
    }
}