package ru.practicum.shareit.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.repository.BookingsRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.BookingStatusAlreadyChangedException;
import ru.practicum.shareit.exceptions.ItemAccessException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.mapper.ModelMapperUtil;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingsRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.BookingStatusAlreadyChangedException;
import ru.practicum.shareit.exceptions.ItemAccessException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.mapper.ModelMapperUtil;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    @Mock
    private final BookingsRepository bookingsRepository;

    @InjectMocks
    private final ItemService itemService;
    @InjectMocks
    private final UserService userService;
    @InjectMocks
    private final BookingService bookingService;

    private final EntityManager em;
    private final ModelMapperUtil mapper;
    private static Pageable page;
    private static LocalDateTime now;
    private static LocalDateTime past;
    private static LocalDateTime future;


    private ItemDto itemDto;
    private UserDto userDto;
    private UserDto userDto2;
    private final Integer FROM = 0;
    private final Integer SIZE = 10;
    private CommentDto commentDto;
    private ResponseBookingDto responseBookingDto;
    private BookingDto bookingDto;

    @BeforeAll
    public static void beforeAll() {
        page = PageRequest.of(0, 2);
        now = LocalDateTime.now();
        past = LocalDateTime.of(2021, 12, 5, 1, 1);
        future = LocalDateTime.of(3000, 12, 5, 1, 1);
    }

    @BeforeEach
    public void setup() {
        userDto = new UserDto("name", "email@ya.ru");
        userDto = userService.addUser(userDto);
        userDto2 = new UserDto("name2", "email2@ya.ru");
        userDto2 = userService.addUser(userDto2);
        itemDto = new ItemDto("name", "description", true);
        itemDto = itemService.addItem(itemDto, userDto.getId());
        bookingDto = new BookingDto(
                itemDto.getId(),
                userDto2,
                itemDto,
                LocalDateTime.of(2020, 12, 5, 1, 1),
                LocalDateTime.of(2025, 12, 7, 1, 1),
                Status.WAITING);
        bookingDto = bookingService.addBooking(userDto2.getId(), bookingDto);
        responseBookingDto = ResponseBookingDto
                .builder()
                .id(1L)
                .itemId(1L)
                .bookerId(1L)
                .build();


    }

    @Test
    @DisplayName("Добавление нового бронирования")
    void saveBookingTest() {

        bookingService.addBooking(userDto2.getId(), bookingDto);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingDto.getId()).getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getId(), equalTo(bookingDto.getId()));
        assertThat(booking.getStatus(), equalTo(bookingDto.getStatus()));
        MatcherAssert.assertThat(booking.getBooker().getId(), equalTo(bookingDto.getBooker().getId()));
        MatcherAssert.assertThat(booking.getItem().getId(), equalTo(bookingDto.getItem().getId()));

    }

    @Test
    @DisplayName("Попытка добавить бронирования владельцем")
    void saveBookingWhenExceptionByOwnerBookTest() {
        lenient().when(bookingsRepository.save(any()))
                .thenThrow(new NotFoundException("Владелец вещи не может ее бронировать"));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(userDto.getId(), bookingDto));

        assertEquals("Владелец вещи не может ее бронировать", exception.getMessage());
    }

    @Test
    @DisplayName("Создание бронирования на вещь, которой нет в БД")
    void saveBookingWhenItemNotFoundTest() {
        bookingDto.setItemId(100L);
        lenient().when(bookingsRepository.save(any()))
                .thenThrow(new NotFoundException(String.format("Вещь с id = %d не найдена", bookingDto.getItemId())));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(userDto2.getId(), bookingDto));

        assertEquals(String.format("Вещь с id = %d не найдена", bookingDto.getItemId()), exception.getMessage());
    }

    @Test
    @DisplayName("Создание бронирования пользовтелем, которого нет в БД")
    void saveBookingWhenUserNotFoundTest() {
        Long userId = 100L;
        lenient().when(bookingsRepository.save(any()))
                .thenThrow(new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(userId, bookingDto));

        assertEquals(String.format("Пользователь с id = %d не найден", userId), exception.getMessage());
    }


    @Test
    @DisplayName("Получение бронирования по id")
    void getBookingByIdTest() {
        bookingService.addBooking(userDto2.getId(), bookingDto);
        lenient().when(bookingsRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(mapper.map(bookingDto, Booking.class)));

        BookingDto bookingDtoActual = bookingService.getById(userDto.getId(), bookingDto.getId());

        assertEquals(bookingDto, bookingDtoActual);
    }

    @Test
    @DisplayName("Получение бронирования по id.Ожидаем исключение,о не найденном бронировании.")
    void getBookingByIdWhenBookingNotFoundTest() {
        Long bookingId = 100L;
        lenient().when(bookingsRepository.save(any()))
                .thenThrow(new NotFoundException(String.format("Бронирование с id = %d не найдено", bookingId)));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getById(userDto.getId(), bookingId));

        assertEquals(String.format("Бронирование с id = %d не найдено", bookingId), exception.getMessage());

    }

    @Test
    @DisplayName("Получение бронирования по id. Должно быть выброшено исключение о том, что пользователь не найден.")
    void getBookingByIdWhenUserNotFoundTest() {
        Long userId = 100L;
        bookingService.addBooking(userDto2.getId(), bookingDto);
        lenient().when(bookingsRepository.save(any()))
                .thenThrow(new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getById(userId, bookingDto.getId()));

        assertEquals(String.format("Пользователь с id = %d не найден", userId), exception.getMessage());
    }

    @Test
    @DisplayName("Получение бронирования по id. Должно быть выброшено исключение о том, что доступ ограничен")
    void getBookingByIdWhenAccessExceptionTest() {
        UserDto userDto3 = userService.addUser(new UserDto("name", "3email@email"));

        bookingService.addBooking(userDto2.getId(), bookingDto);
        lenient().when(bookingsRepository.save(any()))
                .thenThrow(new ItemAccessException("У вас нет доступа к данному бронированию"));

        final ItemAccessException exception = Assertions.assertThrows(
                ItemAccessException.class,
                () -> bookingService.getById(userDto3.getId(), bookingDto.getId()));

        assertEquals("У вас нет доступа к данному бронированию", exception.getMessage());
    }

    @Test
    @DisplayName("Получение всех бронирований заданного пользователя")
    void getAllBookingsOfCurrentUserTest() {
        bookingService.addBooking(userDto2.getId(), bookingDto);
        lenient().when(bookingsRepository.findAllByBookerOrderByStartDesc(mapper.map(userDto2, User.class), page))
                .thenReturn(List.of(bookingDto).stream()
                        .map(bookingDto1 -> mapper.map(bookingDto1, Booking.class))
                        .collect(Collectors.toList()));

        List<BookingDto> expected = bookingService.getAllBookingsOfCurrentUser(userDto2.getId(), State.ALL,FROM,SIZE );
        assertThat(List.of(bookingDto), equalTo(expected));

    }

    @Test
    @DisplayName("Получение всех прошлых бронирований заданного пользователя")
    void getAllBookingsOfCurrentUserPastTest() {
        bookingDto.setEnd(past);
        bookingService.addBooking(userDto2.getId(), bookingDto);
        lenient().when(bookingsRepository.findAllByBookerAndEndIsBeforeOrderByStartDesc(mapper.map(userDto2, User.class), now, page))
                .thenReturn(List.of(bookingDto).stream()
                        .map(bookingDto1 -> mapper.map(bookingDto1, Booking.class))
                        .collect(Collectors.toList()));

        List<BookingDto> expected = bookingService.getAllBookingsOfCurrentUser(userDto2.getId(), State.PAST, FROM,SIZE);
        assertThat(List.of(bookingDto), equalTo(expected));

    }

    @Test
    @DisplayName("Получение всех будущих бронирований заданного пользователя")
    void getAllBookingsOfCurrentUserFutureTest() {
        bookingDto.setStart(future);
        bookingDto.setEnd(future.plusMonths(1));
        bookingService.addBooking(userDto2.getId(), bookingDto);
        lenient().when(bookingsRepository.findAllByBookerAndStartIsAfterOrderByStartDesc(mapper.map(userDto2, User.class), now, page))
                .thenReturn(List.of(bookingDto).stream()
                        .map(bookingDto1 -> mapper.map(bookingDto1, Booking.class))
                        .collect(Collectors.toList()));

        List<BookingDto> expected = bookingService.getAllBookingsOfCurrentUser(userDto2.getId(), State.FUTURE, FROM,SIZE);
        assertThat(List.of(bookingDto), equalTo(expected));

    }

    @Test
    @DisplayName("Получение всех текущих бронирований заданного пользователя")
    void getAllBookingsOfCurrentUserCurrentTest() {
        bookingService.addBooking(userDto2.getId(), bookingDto);
        lenient().when(bookingsRepository.findAllByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(mapper.map(userDto2, User.class), now, now, page))
                .thenReturn(List.of(bookingDto).stream()
                        .map(bookingDto1 -> mapper.map(bookingDto1, Booking.class))
                        .collect(Collectors.toList()));

        List<BookingDto> expected = bookingService.getAllBookingsOfCurrentUser(userDto2.getId(), State.CURRENT, FROM,SIZE);
        assertThat(List.of(bookingDto), equalTo(expected));
    }

    @Test
    @DisplayName("Получение всех бронирований заданного пользователя со статусом WAITING")
    void getAllBookingsOfCurrentUserStatusWaitingTest() {
        bookingService.addBooking(userDto2.getId(), bookingDto);
        lenient().when(bookingsRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(mapper.map(userDto2, User.class), Status.WAITING, page))
                .thenReturn(List.of(bookingDto).stream()
                        .map(bookingDto1 -> mapper.map(bookingDto1, Booking.class))
                        .collect(Collectors.toList()));

        List<BookingDto> expected = bookingService.getAllBookingsOfCurrentUser(userDto2.getId(), State.WAITING,FROM,SIZE);
        assertThat(List.of(bookingDto), equalTo(expected));
    }


    @Test
    @DisplayName("Получение всех бронирований владельцем")
    void getAllBookingsOfOwnerTest() {
        bookingService.addBooking(userDto2.getId(), bookingDto);
        lenient().when(bookingsRepository.findAllByItem_OwnerOrderByStartDesc(mapper.map(userDto, User.class), page))
                .thenReturn(List.of(bookingDto).stream()
                        .map(bookingDto1 -> mapper.map(bookingDto1, Booking.class))
                        .collect(Collectors.toList()));

        List<BookingDto> expected = bookingService.getByOwner(userDto.getId(), State.ALL,FROM,SIZE);
        assertThat(List.of(bookingDto), equalTo(expected));

    }

    @Test
    @DisplayName("Получение всех прошедших бронирований владельцем")
    void getAllBookingsOfOwnerPastTest() {
        bookingDto.setEnd(past);
        bookingService.addBooking(userDto2.getId(), bookingDto);
        lenient().when(bookingsRepository.findAllByItem_OwnerAndEndBeforeOrderByStartDesc(mapper.map(userDto, User.class), now, page))
                .thenReturn(List.of(bookingDto).stream()
                        .map(bookingDto1 -> mapper.map(bookingDto1, Booking.class))
                        .collect(Collectors.toList()));

        List<BookingDto> expected = bookingService.getByOwner(userDto.getId(), State.PAST, FROM,SIZE);
        assertThat(List.of(bookingDto), equalTo(expected));

    }

    @Test
    @DisplayName("Получение всех будущих бронирований владельцем")
    void getAllBookingsOfOwnerFutureTest() {
        bookingDto.setStart(future);
        bookingDto.setEnd(future.plusMonths(1));
        bookingService.addBooking(userDto2.getId(), bookingDto);
        lenient().when(bookingsRepository.findAllByItem_OwnerAndStartIsAfterOrderByStartDesc(mapper.map(userDto, User.class), now, page))
                .thenReturn(List.of(bookingDto).stream()
                        .map(bookingDto1 -> mapper.map(bookingDto1, Booking.class))
                        .collect(Collectors.toList()));

        List<BookingDto> expected = bookingService.getByOwner(userDto.getId(), State.FUTURE, FROM,SIZE);
        assertThat(List.of(bookingDto), equalTo(expected));

    }

    @Test
    @DisplayName("Получение всех текущих бронирований владельцем")
    void getAllBookingsOfOwnerCurrentTest() {
        bookingService.addBooking(userDto2.getId(), bookingDto);
        lenient().when(bookingsRepository.findAllByItem_OwnerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(mapper.map(userDto, User.class), now, now, page))
                .thenReturn(List.of(bookingDto).stream()
                        .map(bookingDto1 -> mapper.map(bookingDto1, Booking.class))
                        .collect(Collectors.toList()));

        List<BookingDto> expected = bookingService.getByOwner(userDto.getId(), State.CURRENT, FROM,SIZE);
        assertThat(List.of(bookingDto), equalTo(expected));
    }

    @Test
    @DisplayName("Получение всех бронирований владельцем со статусом WAITING")
    void getAllBookingsOfOwnerStatusWaitingTest() {
        bookingService.addBooking(userDto2.getId(), bookingDto);
        lenient().when(bookingsRepository.findAllByItem_OwnerEqualsAndStatusEqualsOrderByStartDesc(mapper.map(userDto, User.class), Status.WAITING, page))
                .thenReturn(List.of(bookingDto).stream()
                        .map(bookingDto1 -> mapper.map(bookingDto1, Booking.class))
                        .collect(Collectors.toList()));

        List<BookingDto> expected = bookingService.getByOwner(userDto.getId(), State.WAITING, FROM,SIZE);
        assertThat(List.of(bookingDto), equalTo(expected));
    }

    @Test
    @DisplayName("Получение всех бронирований владельцем со статусом REJECTED")
    void getAllBookingsOfOwnerStatusRejectTest() {
        bookingDto.setStatus(Status.REJECTED);
        bookingService.addBooking(userDto2.getId(), bookingDto);
        bookingService.approveOrRejectBooking(userDto.getId(), bookingDto.getId(), false);
        lenient().when(bookingsRepository.findAllByItem_OwnerEqualsAndStatusEqualsOrderByStartDesc(mapper.map(userDto, User.class), Status.REJECTED, page))
                .thenReturn(List.of(bookingDto).stream()
                        .map(bookingDto1 -> mapper.map(bookingDto1, Booking.class))
                        .collect(Collectors.toList()));

        List<BookingDto> expected = bookingService.getByOwner(userDto.getId(), State.REJECTED,FROM,SIZE);
        assertThat(List.of(bookingDto), equalTo(expected));
    }

    @Test
    @DisplayName("Подтверждение бронирования")
    void approveOrRejectWhenTrueTest() {

        bookingService.addBooking(userDto2.getId(), bookingDto);
        lenient().when(bookingsRepository.save(mapper.map(bookingDto, Booking.class)))
                .thenReturn(mapper.map(bookingDto, Booking.class));
        BookingDto expected = bookingService.approveOrRejectBooking(userDto.getId(), bookingDto.getId(), true);

        assertThat(expected.getStatus(), equalTo(Status.APPROVED));

    }

    @Test
    @DisplayName("Отказ в бронировании")
    void approveOrRejectWhenFalseTest() {

        bookingService.addBooking(userDto2.getId(), bookingDto);
        lenient().when(bookingsRepository.save(mapper.map(bookingDto, Booking.class)))
                .thenReturn(mapper.map(bookingDto, Booking.class));
        BookingDto expected = bookingService.approveOrRejectBooking(userDto.getId(), bookingDto.getId(), false);

        assertThat(expected.getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    @DisplayName("Проброшено исключение о том, что пользователь не найден при подтверждении бронирования")
    void approveOrRejectWhenUserNotFoundTest() {
        Long userId = 100L;
        bookingService.addBooking(userDto2.getId(), bookingDto);
        lenient().when(bookingsRepository.save(mapper.map(bookingDto, Booking.class)))
                .thenThrow(new NotFoundException(String.format("Пользователь с id = %d не найден ", userId)));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.approveOrRejectBooking(userId, bookingDto.getId(), true));
        assertEquals(String.format("Пользователь с id = %d не найден ", userId), exception.getMessage());
    }

    @Test
    @DisplayName("Подтверждение не существующего бронирования.Проброшено исключение.")
    void approveOrRejectWhenBookingNotFoundTest() {
        Long bookingId = 100L;
        lenient().when(bookingsRepository.save(mapper.map(bookingDto, Booking.class)))
                .thenThrow(new NotFoundException(String.format("Бронирование с id = %d не найдено", bookingId)));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.approveOrRejectBooking(userDto.getId(), bookingId, true));
        assertEquals(String.format("Бронирование с id = %d не найдено", bookingId), exception.getMessage());
    }

    @Test
    @DisplayName("Подтвержение бронирования.Проброшено исключение.")
    void approveOrRejectWhenItemAccessExceptionTest() {
        bookingService.addBooking(userDto2.getId(), bookingDto);
        lenient().when(bookingsRepository.save(mapper.map(bookingDto, Booking.class)))
                .thenThrow(new ItemAccessException("Вы не являетесь владельцем бронируемой вещи"));

        final ItemAccessException exception = Assertions.assertThrows(
                ItemAccessException.class,
                () -> bookingService.approveOrRejectBooking(userDto2.getId(), bookingDto.getId(), true));
        assertEquals("Вы не являетесь владельцем бронируемой вещи", exception.getMessage());
    }

    @Test
    @DisplayName("Подтвреждение бронирования.Проброшено исключение, статус уже изменен.")
    void approveOrRejectWhenStatusAlreadyChangedTest() {
        bookingService.addBooking(userDto2.getId(), bookingDto);
        bookingService.approveOrRejectBooking(userDto.getId(), bookingDto.getId(), true);
        lenient().when(bookingsRepository.save(mapper.map(bookingDto, Booking.class)))
                .thenThrow(new BookingStatusAlreadyChangedException("Статут уже обновлен"));

        final BookingStatusAlreadyChangedException exception = Assertions.assertThrows(
                BookingStatusAlreadyChangedException.class,
                () -> bookingService.approveOrRejectBooking(userDto.getId(), bookingDto.getId(), true));
        assertEquals("Статут уже обновлен", exception.getMessage());
    }

}

