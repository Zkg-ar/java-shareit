package ru.practicum.shareit.exceptions;

public class NoSuchStateException extends RuntimeException {
    public NoSuchStateException(String message) {
        super(message);
    }
}
