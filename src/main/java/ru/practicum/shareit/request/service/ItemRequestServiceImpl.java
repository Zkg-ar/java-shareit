package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.mapper.ItemRequestMapper;
import ru.practicum.shareit.mapper.UserMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserService userService;

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;


    @Override
    public ResponseItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = UserMapper.INSTANCE.toUser(userService.getUserById(userId));
        ItemRequest itemRequest = ItemRequestMapper.INSTANCE.toItemRequest((itemRequestDto));
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        return ItemRequestMapper.INSTANCE.toResponseItemRequestDto(itemRequestRepository.save(itemRequest), new ArrayList<>());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseItemRequestDto> getAll(Long userId) {
        userService.getUserById(userId);

        List<ResponseItemRequestDto> requests = itemRequestRepository.findAllByRequester_IdOrderByCreatedDesc(userId)
                .stream()
                .map(
                        itemRequest -> ItemRequestMapper.INSTANCE.toResponseItemRequestDto(itemRequest,
                                itemRepository.findByRequestId(itemRequest.getId())
                                        .stream()
                                        .map(ItemMapper.INSTANCE::toItemDto)
                                        .collect(Collectors.toList())))
                .collect(Collectors.toList());

        List<ItemDto> items = itemRepository
                .findAll()
                .stream()
                .map(ItemMapper.INSTANCE::toItemDto).collect(Collectors.toList());
        for (ResponseItemRequestDto request : requests) {
            request.setItems(items
                    .stream()
                    .filter(itemDto -> itemDto.getRequestId() == request.getId())
                    .collect(Collectors.toList()));
        }

        return requests;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseItemRequestDto> getAllWithPagination(Long userId, Pageable page) {
        userService.getUserById(userId);

        Page<ResponseItemRequestDto> requests = itemRequestRepository
                .findItemRequestByRequester_IdNot(userId, page)
                .map(
                        itemRequest -> ItemRequestMapper.INSTANCE.toResponseItemRequestDto(itemRequest,
                                itemRepository.findByRequestId(itemRequest.getId())
                                        .stream().map(ItemMapper.INSTANCE::toItemDto)
                                        .collect(Collectors.toList()))
                );

        List<ItemDto> items = itemRepository
                .findAll()
                .stream()
                .map(ItemMapper.INSTANCE::toItemDto)
                .collect(Collectors.toList());
        for (ResponseItemRequestDto request : requests) {
            request.setItems(items
                    .stream()
                    .filter(itemDto -> itemDto.getRequestId() == request.getId())
                    .collect(Collectors.toList()));
        }


        return requests.getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseItemRequestDto getById(Long userId, Long requestId) {
        userService.getUserById(userId);
        ResponseItemRequestDto responseItemRequestDto = itemRequestRepository
                .findById(requestId)
                .map(
                        itemRequest -> ItemRequestMapper.INSTANCE.toResponseItemRequestDto(itemRequest,
                                itemRepository.findByRequestId(itemRequest.getId())
                                        .stream()
                                        .map(ItemMapper.INSTANCE::toItemDto)
                                        .collect(Collectors.toList()))
                )
                .orElseThrow(() -> new NotFoundException(String.format("Запрос на вещь с id = %d не найдена", requestId)));
        responseItemRequestDto.setItems(itemRepository.findByRequestId(requestId)
                .stream()
                .map(ItemMapper.INSTANCE::toItemDto)
                .collect(Collectors.toList()));
        return responseItemRequestDto;
    }
}
