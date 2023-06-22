
package ru.practicum.shareit.item.storage;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.Storage;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryItemStorage extends Storage<Item> {
    private Map<Long, Item> items = new HashMap<>();

    @Override
    public Item add(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }


    @Override
    public void delete(Long id) {

    }

    @Override
    public Item getById(Long id) {
        return items.values()
                .stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id = %d не найдена", id)));
    }

    @Override
    public Item update(Item item) {
        return null;
    }

    @Override
    public List<Item> getAll() {
        return items.values()
                .stream()
                .collect(Collectors.toList());
    }

    public List<Item> search(String text) {
        return items.values().stream()
                .filter(item -> item.getName().contains(text.toLowerCase()))
                .filter(item -> item.getDescription().contains(text.toLowerCase()))
                .filter(item -> item.getAvailable() == true)
                .collect(Collectors.toList());
    }
}

