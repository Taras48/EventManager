package com.tk.eventmanager.runner;

import com.tk.eventmanager.model.Event;
import com.tk.eventmanager.service.EventService;
import com.tk.eventmanager.service.LocationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DemoRunner implements CommandLineRunner {

    private final EventService eventService;
    private final LocationService locationService;

    public DemoRunner(
            EventService eventService,
            LocationService locationService) {
        this.eventService = eventService;
        this.locationService = locationService;
    }

    @Override
    public void run(String... args) {
        System.out.println("\n=== ДЕМО ===");

        eventService.createEvent("Java Meetup");
        eventService.createEvent("Spring Workshop");

        locationService.createLocation("loc-1", "address - 1");
        locationService.createLocation("loc-2", "address - 2");

        var locations = locationService.getAllLocations();
        System.out.println("Всего локаций " + locations.size());
        locations.forEach(e -> System.out.println("  → " + e));

        List<Event> events = eventService.getAllEvents();
        System.out.println("Всего событий: " + events.size());
        events.forEach(e -> System.out.println("  → " + e));
    }
}
