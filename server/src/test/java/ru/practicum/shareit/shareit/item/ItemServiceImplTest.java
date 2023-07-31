package ru.practicum.shareit.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.mapper.ModelMapperUtil;
import ru.practicum.shareit.user.dto.UserDto;
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
public class ItemServiceImplTest {
    @Mock
    private final ItemRepository itemRepository;
    @Mock
    private final CommentRepository commentRepository;

    @InjectMocks
    private final ItemService itemService;
    @InjectMocks
    private final UserService userService;
    @InjectMocks
    private final BookingService bookingService;

    private final EntityManager em;
    private final ModelMapperUtil mapper;

    private ItemDto itemDto;
    private UserDto userDto;
    private UserDto userDto2;
    private CommentDto commentDto;
    private BookingDto bookingDto;

    @BeforeEach
    public void setup() {
        userDto = userService.addUser(new UserDto("name", "email@ya.ru"));
        userDto = userService.addUser(userDto);
        userDto2 = userService.addUser(new UserDto("name", "email1@ya.ru"));
        userDto2 = userService.addUser(userDto2);
        itemDto = itemService.addItem(new ItemDto("name", "description", false), userDto.getId());
        itemDto = itemService.addItem(itemDto, userDto.getId());
        bookingDto = new BookingDto(
                itemDto.getId(),
                userDto2,
                itemDto,
                LocalDateTime.of(2020, 12, 5, 1, 1),
                LocalDateTime.of(2021, 12, 7, 1, 1),
                Status.APPROVED);
        commentDto = new CommentDto("Cool", "Zaven", LocalDateTime.of(2023, 4, 29, 12, 0));

        bookingDto = bookingService.addBooking(userDto2.getId(), bookingDto);
    }

    @Test
    @DisplayName("Сохранение новой вещи")
    void saveItemTest() {
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDto.getId()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getRequestId(), equalTo(itemDto.getRequestId()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));

    }

    @Test
    @DisplayName("Получение вещи по id")
    void getItemById() {
        lenient().when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(ItemMapper.INSTANCE.toItem(itemDto)));

        Item item = ItemMapper.INSTANCE.toItem(itemService.getItemById(userDto.getId(), itemDto.getId()));

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getAvailable(), itemDto.getAvailable());

    }


    @Test
    @DisplayName("Получение вещи по id.Выброшено исключение:пользователь не найден")
    void getItemByIdWhenUserNotFoundTest() {
        Long userId = 100L;
        Long itemId = 1L;
        lenient().when(itemRepository.findById(itemId))
                .thenThrow(new NotFoundException(String.format("Пользователь с id = %d не найден!", userId)));


        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getItemById(userId, itemId));

        Assertions.assertEquals(String.format("Пользователь с id = %d не найден!", userId), exception.getMessage());
    }

    @Test
    @DisplayName("Получение вещи по id.Выброшено исключение:вещь не найдена")
    void getItemByIdWhenItemNotFoundTest() {
        Long itemId = 100L;
        lenient().when(itemRepository.findById(itemId))
                .thenThrow(new NotFoundException(String.format("Вещь с id = %d не найдена", itemId)));


        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getItemById(userDto.getId(), itemId));

        Assertions.assertEquals(String.format("Вещь с id = %d не найдена", itemId), exception.getMessage());
    }

    @Test
    @DisplayName("Получение всех вещей")
    void getAllItemsTest() {

        TypedQuery<Item> query = em.createQuery(
                "SELECT item " +
                        "FROM Item item",
                Item.class);
        List<Item> items = query.getResultList();
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo(itemDto.getId()));

    }


    @Test
    @DisplayName("Обновление вещи по ее id")
    void updateItemName() {
        itemDto.setName("newItem");
        itemService.updateItem(itemDto, userDto.getId(), itemDto.getId());

        TypedQuery<Item> query = em.createQuery("SELECT i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDto.getId()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));

    }

    @Test
    @DisplayName("Добавление комментария к вещи.Выброшено исключение:пользователь не найден")
    void addCommentWhenUserNotFound() {
        Long userId = 100L;
        lenient().when(commentRepository.save(any()))
                .thenThrow(new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(userId, itemDto.getId(), commentDto));

        Assertions.assertEquals(String.format("Пользователь с id = %d не найден", userId), exception.getMessage());
    }

    @Test
    @DisplayName("Добавление комментария к вещи.Выброшено исключение:вещь не найден")
    void addCommentWhenItemNotFound() {
        Long itemId = 100L;
        lenient().when(commentRepository.save(any()))
                .thenThrow(new NotFoundException(String.format("Вещь с id = %d не найден", itemId)));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(userDto.getId(), itemId, commentDto));

        Assertions.assertEquals(String.format("Вещь с id = %d не найден", itemId), exception.getMessage());
    }


    @Test
    @DisplayName("Поиск вещей по ключевым словам")
    void searchTest() {
        lenient().when(itemRepository.findItemByText(anyString(), any())).
                thenReturn(List.of(itemDto).stream()
                        .map(ItemMapper.INSTANCE::toItem)
                        .collect(Collectors.toList()));

        List<ItemDto> items = itemService.search("name", 0, 10)
                .stream()
                .map(item -> mapper.map(item, ItemDto.class))
                .collect(Collectors.toList());

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getName(), equalTo(itemDto.getName()));
        assertThat(items.get(0).getId(), equalTo(itemDto.getId()));

    }
}

