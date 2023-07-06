package ru.practicum.shareit.exceptions;

public class BookingNotFoundExceptions extends RuntimeException{
    public BookingNotFoundExceptions(String message){
        super(message);
    }
}
