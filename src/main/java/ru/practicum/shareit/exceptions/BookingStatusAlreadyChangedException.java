package ru.practicum.shareit.exceptions;

public class BookingStatusAlreadyChangedException extends RuntimeException {
    public BookingStatusAlreadyChangedException(String message) {
        super(message);
    }
}
