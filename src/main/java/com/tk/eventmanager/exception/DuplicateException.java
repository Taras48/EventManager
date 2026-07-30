package com.tk.eventmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)  // ← автоматически 409
public class DuplicateException extends RuntimeException {

    public DuplicateException(String message) {
        super(message);
    }
}