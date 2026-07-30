package com.tk.eventmanager.repository;

import com.tk.eventmanager.model.Location;

import java.util.List;
import java.util.Optional;

public interface LocationRepository {
    Location save(Location event);
    Optional<Location> findById(Long id);
    List<Location> findAll();
    void deleteById(Long id);
}
