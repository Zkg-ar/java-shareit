package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import ru.practicum.shareit.booking.model.State;


import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long userId, BookingDto bookingDto);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getByOwner(Long userId, State state, Integer from, Integer size);

    List<BookingDto> getAllBookingsOfCurrentUser(Long userId, State state, Integer from, Integer size);

    BookingDto approveOrRejectBooking(Long userId, Long bookingId, Boolean approved);
}
