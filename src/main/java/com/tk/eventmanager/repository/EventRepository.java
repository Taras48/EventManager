package com.tk.eventmanager.repository;

import com.tk.eventmanager.dto.EventSummary;
import com.tk.eventmanager.model.Event;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends
        JpaRepository<Event, Long>,
        JpaSpecificationExecutor<Event> {

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

    @Query("""
        SELECT e.id AS id, 
               e.title AS title, 
               e.status AS status, 
               e.eventDate AS eventDate, 
               e.capacity AS capacity,
               l.name AS locationName
        FROM Event e 
        LEFT JOIN e.location l
        """)
    Page<EventSummary> findSummaries(Pageable pageable);

    // Пессимистичная блокировка: SELECT ... FOR UPDATE
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.id = :id")
    Optional<Event> findByIdForUpdate(@Param("id") Long id);
}