package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import ru.practicum.shareit.booking.model.Status;

import java.rmi.AccessException;
import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long userId,BookingDto bookingDto);
    BookingDto getById(Long userId,Long bookingId) throws AccessException;

    List<BookingDto> getByOwner(Long userId, Status status);

    List<BookingDto> getAllBookingsOfCurrentUser(Long userId, Status status);

    BookingDto approveOrRejectBooking(Long userId,Long bookingId,Boolean approved);
}
