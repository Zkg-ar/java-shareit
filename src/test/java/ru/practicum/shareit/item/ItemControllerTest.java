package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;


    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());


    private MockMvc mvc;

    private ItemDto itemDto;
    private ItemDtoWithBookings itemDtoWithBookings;
    private CommentDto commentDto;
    private ResponseBookingDto responseBookingDto;


    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
        itemDto = new ItemDto(1L, "name", "description", true, 1L);
        responseBookingDto = ResponseBookingDto
                .builder()
                .id(1L)
                .itemId(1L)
                .bookerId(1L)
                .build();
        itemDtoWithBookings = new ItemDtoWithBookings(1L, "name", "description", true, responseBookingDto, responseBookingDto, new ArrayList<>());
        commentDto = new CommentDto(1L, "Cool", "Zaven", LocalDateTime.of(2023, 4, 29, 12, 0));

    }

    @Test
    @DisplayName("POST-запрос на добавление вещи")
    void saveItemTest() throws Exception {
        when(itemService.addItem(any(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET-запрос получения вещи по id")
    void getItemByIdTest() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDtoWithBookings);

        mvc.perform(get("/items/2")
                        .content(mapper.writeValueAsString(itemDtoWithBookings))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDtoWithBookings.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithBookings.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoWithBookings.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoWithBookings.getAvailable())))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("GET-запрос на получение всех вещей")
    void getAllItemsTest() throws Exception {
        when(itemService.getAllItems(anyLong(), any()))
                .thenReturn(List.of(itemDtoWithBookings));

        mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(itemDtoWithBookings))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(itemDtoWithBookings.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoWithBookings.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoWithBookings.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoWithBookings.getAvailable())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH-запрос на частичное обновление вещи по id")
    void updateItemTest() throws Exception {
        when(itemService.updateItem(any(), any(), any()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    @DisplayName("Поиск нужной вещи по ключевым словам")
    void searchBySubstringTest() throws Exception {
        when(itemService.search(anyString(), any()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=name")
                        .content(mapper.writeValueAsString(List.of(itemDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemDto.getRequestId()), Long.class));

    }

    @Test
    @DisplayName("Добавление комментариев к вещи по ее id")
    void addCommentTest() throws Exception {
        when(itemService.addComment(anyLong(), any(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(status().isOk());

    }

}
