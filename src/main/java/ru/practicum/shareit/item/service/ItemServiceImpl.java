
package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Storage;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.mapper.ModelMapperUtil;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ModelMapperUtil mapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;


    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        return mapper.map(itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id = %d", itemId))), ItemDto.class);
    }

    @Transactional
    @Override
    public List<ItemDto> getAllItems(Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        return itemRepository.findAllByOwnerOrderById(owner)
                .stream()
                .map(item -> mapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        Item item = mapper.map(itemDto, Item.class);
        item.setOwner(userRepository.findById(userId).
                orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId))));
        item.setAvailable(true);
        return mapper.map(itemRepository.save(item), ItemDto.class);
    }


    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.findItemByText(text)
                .stream()
                .map(item -> mapper.map(item, ItemDto.class))
                .collect(Collectors.toList());

    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id = %d не найдена", itemId)));
        if (item.getOwner().getId() != userId) {
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return mapper.map(itemRepository.save(item), ItemDto.class);
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        Comment comment = mapper.map(commentDto, Comment.class);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(itemRepository.findById(itemId).
                orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id = %d не найден", itemId))));
        comment.setAuthor(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId))));
        return mapper.map(commentRepository.save(comment), CommentDto.class);
    }

}


