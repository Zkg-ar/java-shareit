package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ResponseItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ResponseItemRequestDto> getAll(Long userId);

    List<ResponseItemRequestDto> getAllWithPagination(Long userId,Integer from,Integer size);

    ResponseItemRequestDto getById(Long userId, Long requestId);
}
