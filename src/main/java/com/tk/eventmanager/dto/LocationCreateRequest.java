package com.tk.eventmanager.dto;

import jakarta.validation.constraints.*;

public class LocationCreateRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String address;

    @NotNull
    @Min(1)
    private Integer capacity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}