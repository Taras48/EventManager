package com.tk.eventmanager.repository;

import com.tk.eventmanager.model.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByEventId(Long eventId);
    List<Registration> findByUserId(Long userId);
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
    Page<Registration> findByEventId(Long eventId, Pageable pageable);
}