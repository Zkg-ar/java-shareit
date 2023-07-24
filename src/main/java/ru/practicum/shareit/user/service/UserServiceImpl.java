package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.mapper.ModelMapperUtil;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final ModelMapperUtil mapper;

    @Override
    public UserDto addUser(UserDto userDto) {
        try {
            User user = mapper.map(userDto, User.class);
            UserDto dto = mapper.map(userRepository.save(user), UserDto.class);
            return dto;
        } catch (ConstraintViolationException exception) {
            throw new AlreadyExistException(String.format("Пользователь с email = %s уже существует", userDto.getEmail()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> mapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        return mapper.map(userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден!", id))), UserDto.class);
    }

    @Override
    public UserDto updateUserById(Long id, UserDto userDto) {
        if (userDto.getId() == null) {
            userDto.setId(id);
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден!", id)));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if ((userDto.getEmail() != null) && (userDto.getEmail() != user.getEmail())) {
            if (userRepository.findByEmail(userDto.getEmail())
                    .stream()
                    .filter(u -> u.getEmail().equals(userDto.getEmail()))
                    .allMatch(u -> u.getId().equals(userDto.getId()))) {
                user.setEmail(userDto.getEmail());
            } else {
                throw new AlreadyExistException("Пользователь с E-mail=" + user.getEmail() + " уже существует!");
            }

        }
        return mapper.map(userRepository.save(user), UserDto.class);
    }

    @Override
    public void deleteUser(Long id) {
        if (getUserById(id) == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
        userRepository.deleteById(id);
    }
}
