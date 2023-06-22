
package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
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
    public ItemDto getItemById(@PathVariable Long itemId){
        log.info("Получен запрос на получение вещи по id = {}",itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId){
        log.info("Получен запрос на получения списка всех вещей");
        return itemService.getAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchBySubstring(@RequestParam String text){
        log.info("Запрошен товар в названии или описании которого есть слово {}",text);
        return itemService.search(text);

    }

    @PatchMapping
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long id,@RequestBody ItemDto itemDto){
        log.info("Получен запрос на обновление");
        return itemService.updateItem(itemDto,id);
    }



}
>>>>>>> 2ee12ce350f088f712e5ee22ef9b28d0e6b19fc2
