package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingsRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.ModelMapperUtil;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final ModelMapperUtil mapper;
    private final UserRepository userRepository;
    private final BookingsRepository bookingsRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto addBooking(Long userId, BookingDto bookingDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %d не найдена", bookingDto.getItemId())));
        Booking booking = mapper.map(bookingDto, Booking.class);

        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Владелец вещью не может ее бронировать");
        }

        booking.setStatus(Status.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        checkBookingParams(booking);

        return mapper.map(bookingsRepository.save(booking), BookingDto.class);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = bookingsRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с id = %d не найдено", bookingId)));
        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();
        if (userId.equals(bookerId) || userId.equals(ownerId)) {
            return mapper.map(booking, BookingDto.class);
        }

        throw new ItemAccessException("У вас нет доступа к данному бронированию");
    }

    @Override
    public List<BookingDto> getByOwner(Long userId, State state) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден ", userId)));
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                return bookingsRepository.findAllByItem_OwnerOrderByStartDesc(owner)
                        .stream()
                        .map(booking -> mapper.map(booking, BookingDto.class))
                        .collect(Collectors.toList());
            case PAST:
                return bookingsRepository.findAllByItem_OwnerAndEndBeforeOrderByStartDesc(owner, now)
                        .stream()
                        .map(booking -> mapper.map(booking, BookingDto.class))
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingsRepository.findAllByItem_OwnerAndStartIsAfterOrderByStartDesc(owner, now)
                        .stream()
                        .map(booking -> mapper.map(booking, BookingDto.class))
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingsRepository.findAllByItem_OwnerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(owner, now, now)
                        .stream()
                        .map(booking -> mapper.map(booking, BookingDto.class))
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingsRepository
                        .findAllByItem_OwnerEqualsAndStatusEqualsOrderByStartDesc(owner, Status.REJECTED)
                        .stream()
                        .map(booking -> mapper.map(booking, BookingDto.class))
                        .collect(Collectors.toList());
            case WAITING:
                return bookingsRepository
                        .findAllByItem_OwnerEqualsAndStatusEqualsOrderByStartDesc(owner, Status.WAITING)
                        .stream()
                        .map(booking -> mapper.map(booking, BookingDto.class))
                        .collect(Collectors.toList());
            default:
                throw new NoSuchStateException("Unknown state: UNSUPPORTED_STATUS");

        }
    }

    @Override
    public List<BookingDto> getAllBookingsOfCurrentUser(Long userId, State state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден ", userId)));
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                return bookingsRepository.findAllByBookerOrderByStartDesc(user)
                        .stream()
                        .map(booking -> mapper.map(booking, BookingDto.class))
                        .collect(Collectors.toList());
            case PAST:
                return bookingsRepository.findAllByBookerAndEndIsBeforeOrderByStartDesc(user, now)
                        .stream()
                        .map(booking -> mapper.map(booking, BookingDto.class))
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingsRepository.findAllByBookerAndStartIsAfterOrderByStartDesc(user, now)
                        .stream()
                        .map(booking -> mapper.map(booking, BookingDto.class))
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingsRepository.findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(user, now, now)
                        .stream()
                        .map(booking -> mapper.map(booking, BookingDto.class))
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingsRepository
                        .findAllByBookerAndStatusEqualsOrderByStartDesc(user, Status.REJECTED)
                        .stream()
                        .map(booking -> mapper.map(booking, BookingDto.class))
                        .collect(Collectors.toList());
            case WAITING:
                return bookingsRepository
                        .findAllByBookerAndStatusEqualsOrderByStartDesc(user, Status.WAITING)
                        .stream()
                        .map(booking -> mapper.map(booking, BookingDto.class))
                        .collect(Collectors.toList());
            default:
                throw new NoSuchStateException("Unknown state: UNSUPPORTED_STATUS");

        }
    }

    @Override
    public BookingDto approveOrRejectBooking(Long userId, Long bookingId, Boolean approved) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден ", userId)));
        Booking booking = bookingsRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с id = %d не найдено", bookingId)));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new ItemAccessException("Вы не являетесь владельцем бронируемой вещи");
        }

        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BookingStatusAlreadyChangedException("Статут уже обновлен");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return mapper.map(bookingsRepository.save(booking), BookingDto.class);
    }

    private void checkBookingParams(Booking booking) {
        if (booking.getItem().getAvailable() == false) {
            throw new ItemAlreadyBookedException(String.format("Item %s уже забронирован", booking.getItem()));
        }
        if (booking.getEnd().isBefore(booking.getStart())
                || booking.getStart().equals(booking.getEnd())) {
            throw new DateTimeException("Дата и время начала должно быть раньше времени окончания");
        }
    }
}
