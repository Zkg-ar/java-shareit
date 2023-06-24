package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    @Positive
    private Long id;
    @NotBlank
    private String description;
    @NotBlank
    private User requester;
    private LocalDateTime created;

}
