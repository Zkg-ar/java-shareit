package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingsRepository;
import ru.practicum.shareit.exceptions.BookingNotFoundExceptions;
import ru.practicum.shareit.exceptions.DateTimeException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.ModelMapperUtil;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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

//        if (booking.getEnd().isBefore(booking.getStart())
//                || booking.getStart().equals(booking.getEnd())
//                || booking.getStart() != null
//                || booking.getEnd() != null) {
//            throw new DateTimeException("Проверьте введеные вами значения для даты и времени начала и окончания бронирования");
//        }
        return mapper.map(bookingsRepository.save(booking),BookingDto.class);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) throws AccessException {
        Booking booking = bookingsRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundExceptions(String.format("Бронирование с id = %d не найдено", bookingId)));
        if (booking.getBooker().getId() != userId || booking.getItem().getOwner().getId() == userId) {
            throw new AccessException("У вас нет доступа к данному бронированию");
        }
        return null;
    }

    @Override
    public List<BookingDto> getByOwner(Long userId, Status status ) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id = %d не найден ", userId)));
        return bookingsRepository.findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(userId, status)
                .stream()
                .map(booking -> mapper.map(booking, BookingDto.class))
                .collect(Collectors.toList());
    }


    @Override
    public List<BookingDto> getAllBookingsOfCurrentUser(Long userId, Status status) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id = %d не найден ", userId)));

        return bookingsRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(userId, status)
                .stream()
                .map(booking -> mapper.map(booking, BookingDto.class))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public BookingDto approveOrRejectBooking(Long userId, Long bookingId, Boolean approved) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id = %d не найден ", userId)));
        Booking booking = bookingsRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundExceptions(String.format("Бронирование с id = %d не найдено", bookingId)));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new AccessException("Вы не являетесь владельцем бронируемой вещи");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
            booking.getItem().setAvailable(false);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return mapper.map(booking, BookingDto.class);
    }
}
