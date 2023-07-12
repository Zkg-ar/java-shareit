
package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long id);

    ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId);

    ItemDtoWithBookings getItemById(Long userId, Long itemId);

    List<ItemDtoWithBookings> getAllItems(Long userId);

    List<ItemDto> search(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto responseCommentDto);
}

