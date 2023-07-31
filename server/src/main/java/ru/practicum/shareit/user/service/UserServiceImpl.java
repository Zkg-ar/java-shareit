package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Override
    public UserDto addUser(UserDto userDto) {
        try {
            User user = UserMapper.INSTANCE.toUser(userDto);
            UserDto dto = UserMapper.INSTANCE.toUserDto(userRepository.save(user));
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
                .map(UserMapper.INSTANCE::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        return UserMapper.INSTANCE.toUserDto(userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден!", id))));
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
        return UserMapper.INSTANCE.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        if (getUserById(id) == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
        userRepository.deleteById(id);
    }
}
