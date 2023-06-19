package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
public class User {
    @Positive
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;

}
