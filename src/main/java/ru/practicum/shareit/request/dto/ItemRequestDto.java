package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ItemRequestDto {
    @NotNull(message = "Описание вещи не должно быть пустым")
    private String description;
}
