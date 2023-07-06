package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserAlreadyExist;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.mapper.ModelMapperUtil;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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
            throw new UserAlreadyExist(String.format("Пользователь с email = %s уже существует", userDto.getEmail()));
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> mapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        return mapper.map(userRepository.findById(id).orElseThrow(()->new UserNotFoundException(String.format("Пользователь с id = %d  +  не найден!",id))), UserDto.class);
    }

    //    @Override
//    public UserDto updateUserById(Long id, UserDto userDto) {
//        User user = mapper.map(userDto, User.class);
//        user.setId(id);
//        return mapper.map(userRepository.save(user), UserDto.class);
//    }
    @Override
    public UserDto updateUserById(Long id, UserDto userDto) {
        if (userDto.getId() == null) {
            userDto.setId(id);
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID=" + id + " не найден!"));
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
                throw new UserAlreadyExist("Пользователь с E-mail=" + user.getEmail() + " уже существует!");
            }

        }
        return mapper.map(userRepository.save(user), UserDto.class);
    }

    @Override
    public void deleteUser(Long id) {
        if (getUserById(id) == null) {
            throw new UserNotFoundException(String.format("Пользователь с id = %s не найден", id));
        }
        userRepository.deleteById(id);
    }
}
