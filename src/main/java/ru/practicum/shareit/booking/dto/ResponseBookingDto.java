package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseBookingDto {
    Long id;
    Long itemId;
    Long bookerId;
}
