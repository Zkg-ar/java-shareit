
package ru.practicum.shareit.user.storage;

import lombok.NoArgsConstructor;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.Storage;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserAlreadyExist;
import ru.practicum.shareit.user.model.User;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class InMemoryUserStorage extends Storage<User> {
    private Map<Long, User> users = new HashMap<>();


    @Override
    public User add(User newUser) {
        for (User user : users.values()) {
            if (user.getEmail().equals(newUser.getEmail())) {
                throw new UserAlreadyExist(String.format("Пользователь с email = %s уже существует", newUser.getEmail()));
            }
        }

        newUser.setId(generateId());
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public List<User> getAll() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public User getById(Long id) {
        return users.values()
                .stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", id)));
    }

    public void delete(Long id) {
        users.remove(id);
    }


    @Override
    public User update(User user) {
        User updateUser = getById(user.getId());

        Set<String> emails = users.values().stream().map(User::getEmail).collect(Collectors.toSet());

        if (emails.contains(user.getEmail()) && (!user.getEmail().equals(updateUser.getEmail()))) {
            throw new UserAlreadyExist(String.format("Email %s уже существует.", user.getEmail()));
        }
        if (user.getName() != null && !updateUser.getName().isBlank()) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null && !updateUser.getEmail().isBlank()) {
            updateUser.setEmail(user.getEmail());
        }

        users.put(updateUser.getId(), updateUser);
        return updateUser;
    }

}
