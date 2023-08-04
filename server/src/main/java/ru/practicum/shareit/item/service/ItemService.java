
package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long id);

    ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId);

    ItemDtoWithBookings getItemById(Long userId, Long itemId);

    List<ItemDtoWithBookings> getAllItems(Long userId, Integer from, Integer size);

    List<ItemDto> search(String text, Integer from,Integer size);

    CommentDto addComment(Long userId, Long itemId, CommentDto responseCommentDto);
}

