package com.tk.eventmanager.specification;

import com.tk.eventmanager.model.Event;
import com.tk.eventmanager.model.EventStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class EventSpecifications {

    // Приватный конструктор — утилитарный класс
    private EventSpecifications() {}

    // === Фильтр по статусу ===
    public static Specification<Event> hasStatus(EventStatus status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction();  // всегда true (игнорируем)
            return cb.equal(root.get("status"), status);
        };
    }

    // === Фильтр по локации ===
    public static Specification<Event> hasLocationId(Long locationId) {
        return (root, query, cb) -> {
            if (locationId == null) return cb.conjunction();
            return cb.equal(root.get("location").get("id"), locationId);
        };
    }

    // === Поиск по ключевому слову (title ИЛИ description) ===
    public static Specification<Event> containsKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return cb.conjunction();
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    // === Фильтр по дате: от ===
    public static Specification<Event> eventDateFrom(LocalDateTime from) {
        return (root, query, cb) -> {
            if (from == null) return cb.conjunction();
            return cb.greaterThanOrEqualTo(root.get("eventDate"), from);
        };
    }

    // === Фильтр по дате: до ===
    public static Specification<Event> eventDateTo(LocalDateTime to) {
        return (root, query, cb) -> {
            if (to == null) return cb.conjunction();
            return cb.lessThanOrEqualTo(root.get("eventDate"), to);
        };
    }

    // === Фильтр по минимальной вместимости ===
    public static Specification<Event> minCapacity(int minCapacity) {
        return (root, query, cb) -> {
            if (minCapacity <= 0) return cb.conjunction();
            return cb.greaterThanOrEqualTo(root.get("capacity"), minCapacity);
        };
    }

    // === Только с открытой регистрацией ===
    public static Specification<Event> registrationOpen() {
        return (root, query, cb) -> cb.and(
                cb.equal(root.get("status"), EventStatus.PUBLISHED),
                cb.greaterThan(root.get("capacity"), 0)
        );
    }

    // === Комбинирование всех фильтров ===
    public static Specification<Event> withFilters(
            EventStatus status,
            Long locationId,
            String keyword,
            LocalDateTime from,
            LocalDateTime to,
            Integer minCapacity) {

        return Specification.where(hasStatus(status))
                .and(hasLocationId(locationId))
                .and(containsKeyword(keyword))
                .and(eventDateFrom(from))
                .and(eventDateTo(to))
                .and(minCapacity != null ? minCapacity(minCapacity) : null);
    }
}