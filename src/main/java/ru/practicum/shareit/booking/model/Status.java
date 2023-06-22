<<<<<<< HEAD
package ru.practicum.shareit.booking.model;

public enum Status {
    WAITING("wating"),
    APPROVED("approved"),
    REJECTED("rejected"),
    CANCELED("canceled");


    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static Status getStatus(String status) {
        for (Status elem : values()) {
            if (elem.getStatus().equals(status)) {
                return elem;
            }
        }
        throw new IllegalArgumentException("Такой статус бронирования не существует");
    }
}
=======
package ru.practicum.shareit.booking.model;

public enum Status {
    WAITING("wating"),
    APPROVED("approved"),
    REJECTED("rejected"),
    CANCELED("canceled");


    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static Status getStatus(String status) {
        for (Status elem : values()) {
            if (elem.getStatus().equals(status)) {
                return elem;
            }
        }
        throw new IllegalArgumentException("Такой статус бронирования не существует");
    }
}
>>>>>>> 2ee12ce350f088f712e5ee22ef9b28d0e6b19fc2
