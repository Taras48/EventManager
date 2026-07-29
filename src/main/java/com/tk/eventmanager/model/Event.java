package com.tk.eventmanager.model;

public class Event {
    private Long id;
    private String title;

    public Event() {}

    public Event(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    // геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    @Override
    public String toString() {
        return "Event{id=" + id + ", title='" + title + "'}";
    }
}
