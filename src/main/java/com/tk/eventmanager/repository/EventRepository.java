package com.tk.eventmanager.repository;

import com.tk.eventmanager.model.Event;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Spring Data сам сгенерирует SQL по имени метода!
    List<Event> findByStatus(String status);
    // → SELECT * FROM events WHERE status = ?

    List<Event> findByCapacityGreaterThan(int capacity);
    // → SELECT * FROM events WHERE capacity > ?

    List<Event> findByTitleContainingIgnoreCase(String keyword);
    // → SELECT * FROM events WHERE LOWER(title) LIKE LOWER('%keyword%')
}