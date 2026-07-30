package com.tk.eventmanager.controller;

import com.tk.eventmanager.dto.RegistrationRequest;
import com.tk.eventmanager.dto.RegistrationResponse;
import com.tk.eventmanager.mapper.RegistrationMapper;
import com.tk.eventmanager.model.Registration;
import com.tk.eventmanager.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final RegistrationMapper registrationMapper;

    public RegistrationController(RegistrationService registrationService,
                                  RegistrationMapper registrationMapper) {
        this.registrationService = registrationService;
        this.registrationMapper = registrationMapper;
    }

    // POST /api/registrations → зарегистрироваться
    @PostMapping
    public ResponseEntity<RegistrationResponse> register(
            @Valid @RequestBody RegistrationRequest request) {
        Registration reg = registrationService.register(
                request.getEventId(), request.getUserId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registrationMapper.toResponse(reg));
    }

    @GetMapping(params = "eventId")
    public ResponseEntity<Page<RegistrationResponse>> getByEvent(
            @RequestParam Long eventId,
            Pageable pageable) {
        Page<RegistrationResponse> regs = registrationService
                .getRegistrationsForEvent(eventId, pageable)
                .map(registrationMapper::toResponse);
        return ResponseEntity.ok(regs);
    }

    // DELETE /api/registrations/5 → отменить регистрацию
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        registrationService.cancelRegistration(id);
        return ResponseEntity.noContent().build();
    }
}