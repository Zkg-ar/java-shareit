package ru.practicum.shareit.request;

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
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.mapper.ItemRequestMapper;
import ru.practicum.shareit.mapper.ModelMapperUtil;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceImplTest {
    @Mock
    private final ItemRequestRepository itemRequestRepository;


    @InjectMocks
    private final ItemRequestService itemRequestService;
    @InjectMocks
    private final ItemService itemService;
    @InjectMocks
    private final UserService userService;

    private final EntityManager em;
    private final ItemRequestMapper itemRequestMapper;
    private final ModelMapperUtil mapper;

    private ResponseItemRequestDto responseItemRequestDto;
    private ItemRequestDto itemRequestDto;
    private ItemDto itemDto;

    private UserDto userDto;

    @BeforeEach
    public void setup() {
        userDto = new UserDto("name", "email@ya.ru");
        userDto = userService.addUser(userDto);
        itemRequestDto = new ItemRequestDto("description");
        itemDto = itemService.addItem(new ItemDto("name", "description", true), userDto.getId());
        responseItemRequestDto = itemRequestService.addItemRequest(userDto.getId(), itemRequestDto);
    }

    @Test
    @DisplayName("Создание нового запроса на аренду вещи")
    public void saveRequestTest() {
        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", responseItemRequestDto.getId()).getSingleResult();

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getId(), equalTo(responseItemRequestDto.getId()));
        assertThat(itemRequest.getDescription(), equalTo(responseItemRequestDto.getDescription()));
        assertThat(itemRequest.getCreated(), equalTo(responseItemRequestDto.getCreated()));
    }

    @Test
    @DisplayName("Получение всех запросов пользователя.Выброшено исключение:пользователь не найден")
    public void getAllWhenUserNotFoundTest() {
        Long userId = 10L;
        lenient().when(itemRequestRepository.findAllByRequester_IdOrderByCreatedDesc(userId))
                .thenThrow(new NotFoundException(String.format("Пользователь с id = %d не найден!", userId)));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getAll(userId));

        assertEquals(String.format("Пользователь с id = %d не найден!", userId), exception.getMessage());
    }

    @Test
    @DisplayName("Получение всех запросов пользователя.")
    public void getAllTest() {

        lenient().when(itemRequestRepository.findAllByRequester_IdOrderByCreatedDesc(userDto.getId()))
                .thenReturn(List.of(mapper.map(itemRequestDto, ItemRequest.class)));

        List<ResponseItemRequestDto> requests = itemRequestService.getAll(userDto.getId());

        assertEquals(requests.size(), 1);

        assertEquals(requests.get(0).getId(), responseItemRequestDto.getId());

    }

    @Test
    @DisplayName("Получение всех запросов пользователя постранично.Выброшено исключение:пользователь не найден")
    public void getAllWithPaginationWhenUserNotFoundTest() {
        Long userId = 10L;
        lenient().when(itemRequestRepository.findAllByRequester_IdOrderByCreatedDesc(userId))
                .thenThrow(new NotFoundException(String.format("Пользователь с id = %d не найден!", userId)));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getAll(userId));

        assertEquals(String.format("Пользователь с id = %d не найден!", userId), exception.getMessage());
    }

    @Test
    @DisplayName("Получение всех запросов пользователя постранично.")
    public void getAllWithPaginationTest() {

        lenient().when(itemRequestRepository.findAllByRequester_IdOrderByCreatedDesc(userDto.getId()))
                .thenReturn(List.of(mapper.map(itemRequestDto, ItemRequest.class)));

        List<ResponseItemRequestDto> requests = itemRequestService.getAll(userDto.getId());

        assertEquals(requests.size(), 1);

        assertEquals(requests.get(0).getId(), responseItemRequestDto.getId());

    }

}
