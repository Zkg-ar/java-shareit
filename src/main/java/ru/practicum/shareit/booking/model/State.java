package ru.practicum.shareit.booking.model;

public enum State {
    ALL("ALL"),
    CURRENT("CURRENT"),
    PAST("PAST"),
    WAITING("WAITING"),
    REJECTED("REJECTED"),
    FUTURE("FUTURE"),
    UNSUPPORTED_STATUS("UNSUPPORTED_STATUS");

    private final String state;

    State(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public static State getState(String state) {
        for (State elem : values()) {
            if (elem.getState().equals(state)) {
                return elem;
            }
        }
        throw new IllegalArgumentException("Такого состояния бронирования не существует");
    }
}
