package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody BookingDto bookingDto) {
        log.info("Получен запрос на бронирование {}", bookingDto);
        return bookingService.addBooking(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId) {
        log.info("Получить бронирование по id");
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsOfCurrentUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL") State state,
                                                     @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                                     @RequestParam(value = "size", defaultValue = "20") @Min(1) Integer size) {
        log.info("Получение списка всех бронирований текущего пользователя. ");
        return bookingService.getAllBookingsOfCurrentUser(userId, state, from, size);
    }


    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(defaultValue = "ALL") State state,
                                          @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                          @RequestParam(value = "size", defaultValue = "20") @Min(1) Integer size) {
        log.info("Получение списка бронирований для всех вещей заданного пользователя");
        return bookingService.getByOwner(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrRejectBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam Boolean approved) {
        log.info("Подтверждение или отклонение запроса на бронирование");
        return bookingService.approveOrRejectBooking(userId, bookingId, approved);
    }

}
