package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;


@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Получен запрос на получения списка всех пользователей");
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto user) {
        log.info("Получен запрос на добавление пользователя:{}", user);
        return userClient.saveUser(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("Получен запрос на получения пользователся с id = {}", id);
        return userClient.getUserById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUserById(@PathVariable Long id, @RequestBody UserDto user) {
        log.info("Получен запрос на обновление пользователся с id = {}", id);
        return userClient.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        log.info("Получен запрос на удаление пользователся с id = {}", id);
        return userClient.delete(id);
    }

}
