package com.tk.eventmanager.service;

import com.tk.eventmanager.exception.ResourceNotFoundException;
import com.tk.eventmanager.model.Event;
import com.tk.eventmanager.repository.EventRepository;
import com.tk.eventmanager.repository.LocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  // ← Mockito, не Spring!
class EventServiceTest {

    @Mock  // ← поддельный репозиторий
    private EventRepository eventRepository;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks  // ← реальный сервис, но с поддельными зависимостями
    private EventService eventService;

    @Test
    void getEvent_exists_shouldReturn() {
        // Given
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Test");
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        // When
        Event result = eventService.getEvent(1L);

        // Then
        assertEquals("Test", result.getTitle());
        verify(eventRepository).findById(1L);  // проверили, что вызвали
    }

    @Test
    void getEvent_notExists_shouldThrow404() {
        // Given
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // When + Then
        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.getEvent(999L)
        );

        assertTrue(ex.getMessage().contains("999"));
    }

    @Test
    void deleteEvent_exists_shouldCallDelete() {
        // Given
        when(eventRepository.existsById(1L)).thenReturn(true);

        // When
        eventService.deleteEvent(1L);

        // Then
        verify(eventRepository).deleteById(1L);
    }

    @Test
    void deleteEvent_notExists_shouldThrow() {
        // Given
        when(eventRepository.existsById(999L)).thenReturn(false);

        // When + Then
        assertThrows(ResourceNotFoundException.class,
                () -> eventService.deleteEvent(999L));

        verify(eventRepository, never()).deleteById(anyLong());
    }
}