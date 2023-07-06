
package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на добавления item:{}", itemDto);
        return itemService.addItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,@PathVariable Long itemId) {
        log.info("Получен запрос на получение вещи по id = {}", itemId);
        return itemService.getItemById(userId,itemId);
    }


    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получения списка всех вещей");
        return itemService.getAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchBySubstring(@RequestParam String text) {
        log.info("Запрошен товар в названии или описании которого есть слово {}", text);
        if (text.isEmpty()) {
            return Collections.emptyList();
        }

        return itemService.search(text);

    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на обновление вещи по id = {}", itemId);
        return itemService.updateItem(itemDto, userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,@PathVariable Long itemId,@Valid @RequestBody CommentDto commentDto){
        log.info("Пользователь {} написал комментарий:{}",userId,commentDto);
        return itemService.addComment(userId,itemId,commentDto);
    }

}
