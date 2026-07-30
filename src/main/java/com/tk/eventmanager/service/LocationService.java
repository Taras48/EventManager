package com.tk.eventmanager.service;

import com.tk.eventmanager.model.Location;
import com.tk.eventmanager.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    private final LocationRepository repository;

    public LocationService(LocationRepository repository) {
        this.repository = repository;
        System.out.println("[SERVICE] Мне внедрили репозиторий: " + repository);
    }
    @Transactional
    public Location createLocation(String name, String address) {
        Location location = new Location();
        location.setName(name);
        location.setAddress(address);
        return repository.save(location);
    }
    @Transactional(readOnly = true)
    public List<Location> getAllLocations() {
        return repository.findAll();
    }
    @Transactional(readOnly = true)
    public Optional<Location> getLocation(Long id) {
        return repository.findById(id);
    }
}
