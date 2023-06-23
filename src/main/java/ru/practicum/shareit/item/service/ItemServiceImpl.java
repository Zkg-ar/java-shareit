
package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Storage;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.mapper.ModelMapperUtil;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ModelMapperUtil mapper;
    private final InMemoryItemStorage itemStorage;
    private final Storage<User> userStorage;


    @Override
    public ItemDto getItemById(Long itemId) {

        return mapper.map(itemStorage.getById(itemId), ItemDto.class);
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        return itemStorage.getAll()
                .stream()
                .filter(item -> item.getUser() == userStorage.getById(userId))
                .map(item -> mapper.map(item, ItemDto.class))
                .collect(Collectors.toList());

    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        Item item = mapper.map(itemDto, Item.class);
        item.setUser(userStorage.getById(userId));
        return mapper.map(itemStorage.add(item), ItemDto.class);
    }


    @Override
    public List<ItemDto> search(String text) {
        return itemStorage.search(text)
                .stream()
                .map(item -> mapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId) {
        Item item = itemStorage.getById(itemId);
        if (item.getUser().getId() != userId) {
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

        return mapper.map(itemStorage.update(item), ItemDto.class);
    }
}


