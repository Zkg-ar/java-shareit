package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Добавить новый запрос на вещь");
        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ResponseItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получить список всех своих запросов.");
        return itemRequestService.getAll(userId);
    }

    @GetMapping("/all")
    public List<ResponseItemRequestDto> getAllWithPagination(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                             @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                             @RequestParam(value = "size", defaultValue = "20") Integer size) {
        log.info("Постраничный вывод информации о запросах");
        return itemRequestService.getAllWithPagination(userId, PageRequest.of(from, size, Sort.by("created").descending()));
    }

    @GetMapping("/{requestId}")
    public ResponseItemRequestDto getItemById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                              @PathVariable Long requestId) {
        return itemRequestService.getById(userId, requestId);
    }

}
