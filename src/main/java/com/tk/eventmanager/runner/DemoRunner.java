package com.tk.eventmanager.runner;

import com.tk.eventmanager.model.Event;
import com.tk.eventmanager.model.Location;
import com.tk.eventmanager.service.EventService;
import com.tk.eventmanager.service.LocationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DemoRunner implements CommandLineRunner {

    private final EventService eventService;
    private final LocationService locationService;

    public DemoRunner(EventService eventService, LocationService locationService) {
        this.eventService = eventService;
        this.locationService = locationService;
    }

    @Override
    public void run(String... args) {
        System.out.println("\n=== EventManager + PostgreSQL ===\n");

        // Создаём события
        Event e1 = eventService.createEvent("Java Meetup", "Встреча Java-разработчиков", 50);
        Event e2 = eventService.createEvent("Spring Workshop", "Углублённый Spring", 30);
        System.out.println("Создано: " + e1);
        System.out.println("Создано: " + e2);

        // Создаём локацию
        Location loc = locationService.createLocation("Конференц-зал А", "ул. Ленина, 1");
        System.out.println("Локация: " + loc);

        // Ищем
        System.out.println("\nВсе события: " + eventService.getAllEvents());
        System.out.println("По ID 1: " + eventService.getEvent(1L));
        System.out.println("По статусу DRAFT: " + eventService.getEventsByStatus("DRAFT"));

        // Удаляем
        eventService.deleteEvent(e2.getId());
        System.out.println("\nПосле удаления: " + eventService.getAllEvents());
    }
}