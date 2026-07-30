package com.tk.eventmanager.controller;

import com.tk.eventmanager.dto.LocationCreateRequest;
import com.tk.eventmanager.dto.LocationResponse;
import com.tk.eventmanager.mapper.LocationMapper;
import com.tk.eventmanager.model.Location;
import com.tk.eventmanager.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;
    private final LocationMapper locationMapper;

    public LocationController(LocationService locationService, LocationMapper locationMapper) {
        this.locationService = locationService;
        this.locationMapper = locationMapper;
    }

    @GetMapping
    public ResponseEntity<List<LocationResponse>> getAllLocations() {
        List<LocationResponse> locations = locationService.getAllLocations().stream()
                .map(locationMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> getLocation(@PathVariable Long id) {
        Location location = locationService.getLocation(id)
                .orElseThrow(() -> new RuntimeException("Location not found: " + id));
        return ResponseEntity.ok(locationMapper.toResponse(location));
    }

    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(
            @Valid @RequestBody LocationCreateRequest request) {
        Location location = locationService.createLocation(
                request.getName(), request.getAddress(), request.getCapacity());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(locationMapper.toResponse(location));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}