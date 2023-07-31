package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.repository.ItemRepository;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findItemByText() {
    }

    @Test
    void findAllByOwnerOrderById() {
    }

    @Test
    void findByRequestId() {
    }
}