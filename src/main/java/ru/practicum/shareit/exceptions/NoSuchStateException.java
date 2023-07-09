package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;

public class NoSuchStateException extends RuntimeException {
    public NoSuchStateException(String message) {
        super(message);
    }
}
