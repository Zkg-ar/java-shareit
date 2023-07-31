package ru.practicum.shareit.shareit.request;

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
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
public class RequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;


    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());


    private MockMvc mvc;

    private ResponseItemRequestDto itemRequestDto;


    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();
        itemRequestDto = ResponseItemRequestDto
                .builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Создан запрос на аренду вещи")
    public void addItemRequestTest() throws Exception {
        when(itemRequestService.addItemRequest(anyLong(), any()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated())));
    }

    @Test
    @DisplayName("Получение запроса на вещь по id")
    public void getByIdTest() throws Exception {
        when(itemRequestService.getById(any(), any()))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created[0]", is(itemRequestDto.getCreated().getYear())))
                .andExpect(jsonPath("$.created[1]", is(itemRequestDto.getCreated().getMonthValue())))
                .andExpect(jsonPath("$.created[2]", is(itemRequestDto.getCreated().getDayOfMonth())));
    }

    @Test
    @DisplayName("Получение списка запросов")
    public void getAllTest() throws Exception {
        when(itemRequestService.getAll(anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created[0]", is(itemRequestDto.getCreated().getYear())))
                .andExpect(jsonPath("$[0].created[1]", is(itemRequestDto.getCreated().getMonthValue())))
                .andExpect(jsonPath("$[0].created[2]", is(itemRequestDto.getCreated().getDayOfMonth())));
    }

    @Test
    @DisplayName("Получение списка запросов постранично")
    public void getAllWithPaginationTest() throws Exception {
        when(itemRequestService.getAllWithPagination(anyLong(), anyInt(),anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created[0]", is(itemRequestDto.getCreated().getYear())))
                .andExpect(jsonPath("$[0].created[1]", is(itemRequestDto.getCreated().getMonthValue())))
                .andExpect(jsonPath("$[0].created[2]", is(itemRequestDto.getCreated().getDayOfMonth())));
    }

}
