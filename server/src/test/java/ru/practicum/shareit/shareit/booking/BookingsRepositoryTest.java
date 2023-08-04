package ru.practicum.shareit.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.repository.BookingsRepository;

@DataJpaTest
class BookingsRepositoryTest {
    @Autowired
    private BookingsRepository bookingsRepository;

    @Test
    void findAllByBookerOrderByStartDesc() {
    }

    @Test
    void findAllByBookerAndEndIsBeforeOrderByStartDesc() {
    }

    @Test
    void findAllByBookerAndStartIsAfterOrderByStartDesc() {
    }

    @Test
    void findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
    }

    @Test
    void findAllByBookerAndStatusEqualsOrderByStartDesc() {
    }

    @Test
    void findAllByItem_OwnerOrderByStartDesc() {
    }

    @Test
    void findAllByItem_OwnerAndEndBeforeOrderByStartDesc() {
    }

    @Test
    void findAllByItem_OwnerAndStartIsAfterOrderByStartDesc() {
    }

    @Test
    void findAllByItem_OwnerAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
    }

    @Test
    void findAllByItem_OwnerEqualsAndStatusEqualsOrderByStartDesc() {
    }

    @Test
    void findBookingByItemIdAndStartBeforeOrderByEndDesc() {
    }

    @Test
    void findBookingByItem_IdAndStartAfterAndStatusEqualsOrderByStart() {
    }

    @Test
    void findBookingsByBooker_IdAndItem_IdAndStatusEqualsAndStartBeforeAndEndBefore() {
    }
}