package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingsRepository extends JpaRepository<Booking, Long> {


    @Query("SELECT b FROM Booking b where b.booker = ?1 ORDER BY b.start desc")
    List<Booking> findAllByBookerByStateAll(User user);

    List<Booking> findAllByBookerOrderByStartDesc(User user);

    @Query("SELECT b FROM Booking b where b.booker = ?1 AND b.end < NOW() ORDER BY b.start desc ")
    List<Booking> findAllByBookerByStatePast(User user);

    List<Booking> findAllByBookerAndEndIsBeforeOrderByStartDesc(User user, LocalDateTime now);

    @Query("SELECT b FROM Booking b where b.booker = ?1 AND b.start > NOW() ORDER BY b.start desc ")
    List<Booking> findAllByBookerByStateFuture(User user);

    List<Booking> findAllByBookerAndStartIsAfterOrderByStartDesc(User user, LocalDateTime now);

    @Query("SELECT b FROM Booking b where b.booker = ?1 AND (b.start < NOW() AND b.end >NOW() ) ORDER BY b.start desc")
    List<Booking> findAllByBookerByStateCurrent(User user);

    List<Booking> findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(User user, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerAndStatusEqualsOrderByStartDesc(User user, Status status);


    @Query("SELECT b FROM Booking b WHERE b.item.owner = ?1 ORDER BY b.start desc")
    List<Booking> findAllByItem_OwnerByStateAll(User owner);

    List<Booking> findAllByItem_OwnerOrderByStartDesc(User owner);

    @Query("SELECT b FROM Booking b where b.item.owner = ?1 AND b.end < NOW() ORDER BY b.start desc")
    List<Booking> findAllByItem_OwnerByStatePast(User owner);

    List<Booking> findAllByItem_OwnerAndEndBeforeOrderByStartIdDesc(User owner, LocalDateTime now);

    @Query("SELECT b FROM Booking b where b.item.owner = ?1 AND b.start > NOW() ORDER BY b.start desc ")
    List<Booking> findAllByItem_OwnerAndStateFuture(User owner);

    List<Booking> findAllByItem_OwnerAndStartIsAfterOrderByStartDesc(User owner, LocalDateTime now);

    @Query("SELECT b FROM Booking b where b.item.owner = ?1 AND (b.start < NOW() AND b.end >NOW() ) ORDER BY b.start desc")
    List<Booking> findAllByItem_OwnerByStateCurrent(User owner);

    List<Booking> findAllByItem_OwnerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(User owner,LocalDateTime start,LocalDateTime end);

    List<Booking> findAllByItem_OwnerEqualsAndStatusEqualsOrderByStartDesc(User owner, Status status);


    List<Booking> findBookingByItem_IdAndEndIsBeforeOrderByEndDesc(Long itemId, LocalDateTime time);

    List<Booking> findBookingByItem_IdAndEndIsBeforeAndStatusEqualsOrderByEndDesc(Long itemId, LocalDateTime time, Status status);

    List<Booking> findBookingsByBooker_IdAndItem_IdAndStatusEqualsAndStartBeforeAndEndBefore(Long userId, Long itemId, Status status, LocalDateTime start, LocalDateTime end);
}
