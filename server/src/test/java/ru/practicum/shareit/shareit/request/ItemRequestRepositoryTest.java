package ru.practicum.shareit.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void findAllByRequester_IdOrderByCreatedDesc() {
    }

    @Test
    void findItemRequestByRequester_IdNot() {
    }
}