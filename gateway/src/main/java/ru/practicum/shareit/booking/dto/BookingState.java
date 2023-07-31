package ru.practicum.shareit.booking.dto;

public enum BookingState {
    ALL("ALL"),
    CURRENT("CURRENT"),
    PAST("PAST"),
    WAITING("WAITING"),
    REJECTED("REJECTED"),
    FUTURE("FUTURE"),
    UNSUPPORTED_STATUS("UNSUPPORTED_STATUS");

    private final String state;

    BookingState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public static BookingState from(String state) {
        for (BookingState elem : values()) {
            if (elem.getState().equals(state)) {
                return elem;
            }
        }
        throw new IllegalArgumentException("Такого состояния бронирования не существует");
    }


}
