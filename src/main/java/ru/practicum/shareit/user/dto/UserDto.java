
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
    @NotBlank(message = "Поле email не должно быть пустым.")
    @Email
    private String email;

    public UserDto(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
