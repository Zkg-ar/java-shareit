package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserAlreadyExist;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.mapper.ModelMapperUtil;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
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

        if (getAllUsers().stream().anyMatch(u -> u.getEmail().equals(userDto.getEmail()))) {
            throw new UserAlreadyExist(String.format("Пользователь с email = %s уже существует", userDto.getEmail()));
        }

        User user = mapper.map(getUserById(id), User.class);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return mapper.map(userStorage.update(user), UserDto.class);
    }

    @Override
    public void deleteUser(Long id) {
        getUserById(id);
        userStorage.delete(id);
    }
}
