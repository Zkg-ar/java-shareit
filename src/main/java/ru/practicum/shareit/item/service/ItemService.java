
package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long id);

    ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId);
    ItemDto getItemById(Long ItemId);

    List<ItemDto> getAllItems(Long userId);
    List<ItemDto> search (String text);
}
