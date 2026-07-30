package com.tk.eventmanager.service;

import com.tk.eventmanager.model.Location;
import com.tk.eventmanager.repository.InMemoryLocationRepository;
import com.tk.eventmanager.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    private final LocationRepository repository;

    public LocationService(LocationRepository repository) {
        this.repository = repository;
        System.out.println("[SERVICE] Мне внедрили репозиторий: " + repository);
    }

    public Location createLocation(String name, String address) {
        Location location = new Location();
        location.setName(name);
        location.setAddress(address);
        return repository.save(location);
    }

    public List<Location> getAllLocations() {
        return repository.findAll();
    }

    public Optional<Location> getLocation(Long id) {
        return repository.findById(id);
    }
}
