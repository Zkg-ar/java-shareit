package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;

public interface BookingsRepository extends JpaRepository<Booking, Long> {


    //    @Query("SELECT b from Booking b where  and b.booker.id = ?1 order by b.start desc")
//    List<Booking> findAllByBookerAndStatusOrderByStartDesc(Long id, State state);
//@Transactional
//    List<Booking> findAllByItem_OwnerAndStateEqualsOrderByStartDesc(Long id, State state);
    @Query("SELECT b FROM Booking b where b.booker = ?1 ORDER BY b.start desc")
    List<Booking> findAllByBookerByStateAll(User user);

    @Query("SELECT b FROM Booking b where b.booker = ?1 AND b.end < NOW() ORDER BY b.start desc ")
    List<Booking> findAllByBookerByStatePast(User user);

    @Query("SELECT b FROM Booking b where b.booker = ?1 AND b.start > NOW() ORDER BY b.start desc ")
    List<Booking> findAllByBookerByStateFuture(User user);

    @Query("SELECT b FROM Booking b where b.booker = ?1 AND (b.start < NOW() AND b.end >NOW() ) ORDER BY b.start desc")
    List<Booking> findAllByBookerByStateCurrent(User user);

    List<Booking> findAllByBookerAndStatusEqualsOrderByStartDesc(User user, Status status);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = ?1 ORDER BY b.start desc")
    List<Booking> findAllByItem_OwnerByStateAll(User owner);


    @Query("SELECT b FROM Booking b where b.item.owner = ?1 AND b.end < NOW() ORDER BY b.start desc")
    List<Booking> findAllByItem_OwnerByStatePast(User owner);

    @Query("SELECT b FROM Booking b where b.item.owner = ?1 AND b.start > NOW() ORDER BY b.start desc ")
    List<Booking> findAllByItem_OwnerAndStateFuture(User owner);


    @Query("SELECT b FROM Booking b where b.item.owner = ?1 AND (b.start < NOW() AND b.end >NOW() ) ORDER BY b.start desc")
    List<Booking> findAllByItem_OwnerByStateCurrent(User owner);

    List<Booking> findAllByItem_OwnerEqualsAndStatusEqualsOrderByStartDesc(User owner, Status status);

}
