package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    @NotNull(message = "Описание вещи не должно быть пустым")
    private String description;
}
