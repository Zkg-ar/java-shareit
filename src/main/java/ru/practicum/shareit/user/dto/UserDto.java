package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Data
@NoArgsConstructor
public class UserDto {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}
