package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingsRepository extends JpaRepository<Booking, Long> {


    //Метод возвращает список бронирований.
    List<Booking> findAllByBookerOrderByStartDesc(User user);

    //Метод возвращает список всех прошедших бронирований.
    List<Booking> findAllByBookerAndEndIsBeforeOrderByStartDesc(User user, LocalDateTime now);

    //Метод возвращает список всех будуших бронирований
    List<Booking> findAllByBookerAndStartIsAfterOrderByStartDesc(User user, LocalDateTime now);

    //Метод возвращает список всех текущих бронирований
    List<Booking> findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(User user, LocalDateTime start, LocalDateTime end);

    //Метод возвращает список бронирования по заданному статусу
    List<Booking> findAllByBookerAndStatusEqualsOrderByStartDesc(User user, Status status);

    //Метод возвращает владельцу список бронирований.
    List<Booking> findAllByItem_OwnerOrderByStartDesc(User owner);

    //Метод возвращает владельцу список всех прошедших бронирований.
    List<Booking> findAllByItem_OwnerAndEndBeforeOrderByStartDesc(User owner, LocalDateTime now);

    //Метод владельцу возвращает список всех будуших бронировани
    List<Booking> findAllByItem_OwnerAndStartIsAfterOrderByStartDesc(User owner, LocalDateTime now);

    //Метод владельцу возвращает список всех текущих бронирований
    List<Booking> findAllByItem_OwnerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(User owner, LocalDateTime start, LocalDateTime end);

    //Метод возвращает владельцу список бронирования по заданному статусу
    List<Booking> findAllByItem_OwnerEqualsAndStatusEqualsOrderByStartDesc(User owner, Status status);

    List<Booking> findBookingByItemIdAndStartBeforeOrderByEndDesc(Long itemId, LocalDateTime time);

    List<Booking> findBookingByItem_IdAndStartAfterAndStatusEqualsOrderByStart(Long itemId, LocalDateTime time, Status status);

    List<Booking> findBookingsByBooker_IdAndItem_IdAndStatusEqualsAndStartBeforeAndEndBefore(Long userId, Long itemId, Status status, LocalDateTime start, LocalDateTime end);
}
