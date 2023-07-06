package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.util.List;

public interface BookingsRepository extends JpaRepository<Booking,Long> {



    List<Booking>findAllByBookerAndStatusEqualsOrderByStartDesc(Long id,Status status);
    List<Booking>findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(Long id, Status status);
}
