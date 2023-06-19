package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Positive;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
public class Booking {
    @Positive
    private Long id;

    private LocalDateTime start;
    private LocalDateTime end;

    private Item item;
    private User booker;

    private Status status;


}
