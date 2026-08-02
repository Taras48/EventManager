package com.tk.eventmanager.model;

import com.tk.eventmanager.exception.BadRequestException;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(nullable = false)
    private int capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status = EventStatus.DRAFT;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // === STATE MACHINE: переходы ===

    public void publish() {
        if (this.status != EventStatus.DRAFT) {
            throw new BadRequestException(
                    "Cannot publish event in status " + this.status + ". Only DRAFT can be published.");
        }
        this.status = EventStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status != EventStatus.DRAFT && this.status != EventStatus.PUBLISHED) {
            throw new BadRequestException(
                    "Cannot cancel event in status " + this.status + ". Only DRAFT or PUBLISHED can be canceled.");
        }
        this.status = EventStatus.CANCELED;
        this.canceledAt = LocalDateTime.now();
    }

    public void complete() {
        if (this.status != EventStatus.PUBLISHED) {
            throw new BadRequestException(
                    "Cannot complete event in status " + this.status + ". Only PUBLISHED can be completed.");
        }
        this.status = EventStatus.COMPLETED;
    }

    public void archive() {
        if (this.status != EventStatus.COMPLETED) {
            throw new BadRequestException(
                    "Cannot archive event in status " + this.status + ". Only COMPLETED can be archived.");
        }
        this.status = EventStatus.ARCHIVED;
    }

    // === БИЗНЕС-ПРОВЕРКИ ===

    public boolean isRegistrationOpen() {
        return this.status == EventStatus.PUBLISHED
                && this.capacity > 0
                && (this.eventDate == null || this.eventDate.isAfter(LocalDateTime.now()));
    }

    public boolean canBeCanceled() {
        return this.status == EventStatus.DRAFT || this.status == EventStatus.PUBLISHED;
    }

    // Конструкторы, геттеры, сеттеры
    public Event() {}

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
    public EventStatus getStatus() { return status; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public LocalDateTime getCanceledAt() { return canceledAt; }
}