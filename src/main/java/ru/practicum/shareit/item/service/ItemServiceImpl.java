
package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Storage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.mapper.ModelMapperUtil;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;
import ru.practicum.shareit.user.storage.UserStorage;

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
    public ItemDto updateItem(ItemDto itemDto, Long id) {
        return null;
    }
}

