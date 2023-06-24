package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.mapper.ModelMapperUtil;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final InMemoryUserStorage userStorage;
    private final ModelMapperUtil mapper;

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = mapper.map(userDto, User.class);
        UserDto dto = mapper.map(userStorage.add(user), UserDto.class);
        return dto;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getAll()
                .stream()
                .map(user -> mapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        return mapper.map(userStorage.getById(id), UserDto.class);
    }

    @Override
    public UserDto updateUserById(Long id, UserDto userDto) {
        User user = mapper.map(userDto, User.class);
        user.setId(id);
        return mapper.map(userStorage.update(user), UserDto.class);
    }

    @Override
    public void deleteUser(Long id) {
        if (getUserById(id) == null) {
            throw new UserNotFoundException(String.format("Пользователь с id = %s не найден", id));
        }
        userStorage.delete(id);
    }
}
