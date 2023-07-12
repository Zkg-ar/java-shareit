package ru.practicum.shareit.exceptions;

public class ItemAlreadyBookedException extends RuntimeException {
    public ItemAlreadyBookedException(String message) {
        super(message);
    }
}
