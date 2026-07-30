package com.tk.eventmanager.repository;

import com.tk.eventmanager.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Spring Data сам сгенерирует SQL по имени метода!
    List<Event> findByStatus(String status);
    // → SELECT * FROM events WHERE status = ?

    List<Event> findByCapacityGreaterThan(int capacity);
    // → SELECT * FROM events WHERE capacity > ?

    List<Event> findByTitleContainingIgnoreCase(String keyword);
    // → SELECT * FROM events WHERE LOWER(title) LIKE LOWER('%keyword%')

    @Query("SELECT e FROM Event e JOIN FETCH e.location WHERE e.id = :id")
    Optional<Event> findByIdWithLocation(@Param("id") Long id);

    @Modifying
    @Query("update Event e set e.id = :id1 where e.id = :id2")
    Event update(@Param("id") Long id, @Param("id") Event event);

    Page<Event> findByStatus(String status, Pageable pageable);

    @EntityGraph(attributePaths = {"location"})
    Page<Event> findAllWithLocation(Pageable pageable);
}