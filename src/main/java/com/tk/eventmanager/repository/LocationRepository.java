package com.tk.eventmanager.repository;

import com.tk.eventmanager.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    // Тоже пустой. Всё работает из коробки.
}