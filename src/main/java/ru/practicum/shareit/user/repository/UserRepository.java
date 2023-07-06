package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    List<User> findByEmail(@NotBlank @Email String email);
}
