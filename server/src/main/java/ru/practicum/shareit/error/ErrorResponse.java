package ru.practicum.shareit.error;


public class ErrorResponse {
    private String error;
    private String message;

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public ErrorResponse(String message) {
        this.message = message;
    }


    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}

