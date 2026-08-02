package com.tk.eventmanager.controller;

import com.tk.eventmanager.dto.EventCreateRequest;
import com.tk.eventmanager.dto.EventResponse;
import com.tk.eventmanager.dto.EventSearchRequest;
import com.tk.eventmanager.mapper.EventMapper;
import com.tk.eventmanager.model.Event;
import com.tk.eventmanager.model.EventStatus;
import com.tk.eventmanager.service.EventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    // GET /api/events?page=0&size=10
    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAllEvents(Pageable pageable) {
        return ResponseEntity.ok(eventService.getAllEvents(pageable).map(eventMapper::toResponse));
    }

    // GET /api/events/5
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventMapper.toResponse(eventService.getEventWithLocation(id)));
    }

    // POST /api/events (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventCreateRequest request) {
        Event event = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventMapper.toResponse(event));
    }

    // PUT /api/events/5 (ADMIN, только DRAFT)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id, @Valid @RequestBody EventCreateRequest request) {
        return ResponseEntity.ok(eventMapper.toResponse(eventService.updateEvent(id, request)));
    }

    // DELETE /api/events/5 (ADMIN, только DRAFT)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    // === ПЕРЕХОДЫ СТАТУСОВ ===

    // POST /api/events/5/publish (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/publish")
    public ResponseEntity<EventResponse> publishEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventMapper.toResponse(eventService.publishEvent(id)));
    }

    // POST /api/events/5/cancel (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<EventResponse> cancelEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventMapper.toResponse(eventService.cancelEvent(id)));
    }

    // POST /api/events/5/complete (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/complete")
    public ResponseEntity<EventResponse> completeEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventMapper.toResponse(eventService.completeEvent(id)));
    }

    // POST /api/events/5/archive (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/archive")
    public ResponseEntity<EventResponse> archiveEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventMapper.toResponse(eventService.archiveEvent(id)));
    }

    // GET /api/events?status=PUBLISHED&page=0&size=10
    @GetMapping(params = "status")
    public ResponseEntity<Page<EventResponse>> getEventsByStatus(
            @RequestParam EventStatus status, Pageable pageable) {
        return ResponseEntity.ok(
                eventService.getEventsByStatus(status, pageable).map(eventMapper::toResponse));
    }

    @GetMapping
    public ResponseEntity<Page<EventResponse>> searchEvents(
            EventSearchRequest search,     // ← Spring сам биндит query-параметры
            Pageable pageable) {

        Page<EventResponse> events = eventService.searchEvents(search, pageable)
                .map(eventMapper::toResponse);

        return ResponseEntity.ok(events);
    }
}