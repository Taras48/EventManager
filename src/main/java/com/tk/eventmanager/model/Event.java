package com.tk.eventmanager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity                          // ← "Hibernate, я таблица!"
@Table(name = "events")          // ← имя таблицы в БД
public class Event {

    @Id                          // ← первичный ключ
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ← автоинкремент
    private Long id;

    @Column(nullable = false)    // ← NOT NULL
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_date") // ← имя колонки (если отличается от поля)
    private LocalDateTime eventDate;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private String status = "DRAFT";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // === СВЯЗЬ: много событий → одна локация ===
    @ManyToOne(fetch = FetchType.LAZY)   // ← тип связи
    @JoinColumn(name = "location_id")     // ← колонка в таблице events
    private Location location;

    // === Жизненный цикл Entity ===
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Конструкторы
    public Event() {}  // ← Hibernate ТРЕБУЕТ пустой конструктор!

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "Event{id=" + id + ", title='" + title + "', status='" + status + "'}";
    }
}