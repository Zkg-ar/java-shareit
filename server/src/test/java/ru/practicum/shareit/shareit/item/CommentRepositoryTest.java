package ru.practicum.shareit.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.repository.CommentRepository;


@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Test
    void findCommentByItemId() {
    }
}