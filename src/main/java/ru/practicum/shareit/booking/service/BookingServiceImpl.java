package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.rmi.AccessException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ModelMapperUtil mapper;
    private final UserRepository userRepository;
    private final BookingsRepository bookingsRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto addBooking(Long userId, BookingDto bookingDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id = %d не найдена", bookingDto.getItemId())));
        Booking booking = mapper.map(bookingDto, Booking.class);
        booking.setStatus(Status.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        checkBookingParams(booking);

        return mapper.map(bookingsRepository.save(booking), BookingDto.class);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = bookingsRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundExceptions(String.format("Бронирование с id = %d не найдено", bookingId)));
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
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id = %d не найден ", userId)));
        switch (state) {
            case ALL:
                return bookingsRepository.findAllByItem_OwnerByStateAll(owner)
                        .stream()
                        .map(booking -> mapper.map(booking, BookingDto.class))
                        .collect(Collectors.toList());
            case PAST:
                return bookingsRepository.findAllByItem_OwnerByStatePast(owner)
                        .stream()
                        .map(booking -> mapper.map(booking, BookingDto.class))
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingsRepository.findAllByItem_OwnerAndStateFuture(owner)
                        .stream()
                        .map(booking -> mapper.map(booking, BookingDto.class))
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingsRepository.findAllByItem_OwnerByStateCurrent(owner)
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
                throw new IllegalArgumentException("UNSUPPORTED_STATUS");

        }
    }


    @Override
    public List<BookingDto> getAllBookingsOfCurrentUser(Long userId, State state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id = %d не найден ", userId)));
        switch (state) {
            case ALL:
                return bookingsRepository.findAllByBookerByStateAll(user)
                        .stream()
                        .map(booking -> mapper.map(booking, BookingDto.class))
                        .collect(Collectors.toList());
            case PAST:
                return bookingsRepository.findAllByBookerByStatePast(user)
                        .stream()
                        .map(booking -> mapper.map(booking, BookingDto.class))
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingsRepository.findAllByBookerByStateFuture(user)
                        .stream()
                        .map(booking -> mapper.map(booking, BookingDto.class))
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingsRepository.findAllByBookerByStateCurrent(user)
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
                throw new IllegalArgumentException("UNSUPPORTED_STATUS");

        }
    }

    @Override
    public BookingDto approveOrRejectBooking(Long userId, Long bookingId, Boolean approved) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id = %d не найден ", userId)));
        Booking booking = bookingsRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundExceptions(String.format("Бронирование с id = %d не найдено", bookingId)));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ItemAccessException("Вы не являетесь владельцем бронируемой вещи");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
            booking.getItem().setAvailable(false);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return mapper.map(booking, BookingDto.class);
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
