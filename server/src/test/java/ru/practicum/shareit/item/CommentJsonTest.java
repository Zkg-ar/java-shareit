package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;


import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentJsonTest {
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testCommentDto() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "Cool", "Zaven", LocalDateTime.of(2023, 4, 29, 12, 0));

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Zaven");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Cool");

    }
}

