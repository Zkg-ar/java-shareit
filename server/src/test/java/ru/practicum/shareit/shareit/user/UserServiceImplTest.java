package ru.practicum.shareit.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {
    @Mock
    private final UserRepository userRepository;
    @InjectMocks
    private final UserService userService;
    private final EntityManager em;

    private UserDto userDto;
    private UserDto userDto1;

    @BeforeEach
    public void setup() {

        userDto = new UserDto( "name", "ru.practicum.shareit.user@yandex");
        userDto1 = new UserDto("name2", "user2@yandex");
        userDto = userService.addUser(userDto);
        userDto1 = userService.addUser(userDto1);

    }

    @Test
    @DisplayName("Создание нового пользователя")
    void saveUserTest() {

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }


    @Test
    @DisplayName("Получение пользователя по id")
    void getUserByIdTest() {


        lenient().when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserMapper.INSTANCE.toUser(userDto)));

        User user = UserMapper.INSTANCE.toUser(userService.getUserById(userDto.getId()));

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());


    }

    @Test
    @DisplayName("Получение пользователя по id.Выброшено исключение:пользователь не найден")
    void getUserByIdTestWhenNotFound() {
        Long id = 100L;
        lenient().when(userRepository.findById(id))
                .thenThrow(new NotFoundException(String.format("Пользователь с id = %s не найден!", id)));


        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(id));
        Assertions.assertEquals(String.format("Пользователь с id = %d не найден!", id), exception.getMessage());
    }

    @Test
    @DisplayName("Получение всех пользователей")
    void getAllUsersTest() {
        TypedQuery<User> query = em.createQuery(
                "SELECT user " +
                        "FROM User user",
                User.class);
        List<User> users = query.getResultList();
        assertThat(users.size(), equalTo(2));

    }

    @Test
    @DisplayName("Обновление имени пользователя")
    void updateUserName() {
        userDto.setName("newName");
        userService.updateUserById(userDto.getId(),userDto);
        TypedQuery<User> query = em.createQuery("SELECT u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", userDto.getId()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));

    }

    @Test
    @DisplayName("Обновление email пользователя")
    void updateUserEmailTest() {
        userDto.setEmail("newEmailUserDto@mail.ru");
        userDto = userService.updateUserById(userDto.getId(),userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", userDto.getId()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }


}

