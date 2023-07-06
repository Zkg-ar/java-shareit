package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingDto {

    private Long id;
    @NotNull
    private Long itemId;
    private User booker;
    @Future
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonAlias({"start"})
    private LocalDateTime startTime;
    @Future
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonAlias({"end"})
    private LocalDateTime endTime;
    private Status status;
}
