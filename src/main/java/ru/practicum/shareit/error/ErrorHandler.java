
package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.*;

import javax.validation.ValidationException;
import java.rmi.AccessException;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final ValidationException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Ошибка валидации:", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Ошибка валидации:", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAccess(final AccessException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Ошибка доступа:", e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(final UserNotFoundException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Ошибка 404:", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingNotFound(final BookingNotFoundExceptions e) {
        log.error(e.getMessage());
        return new ErrorResponse("Ошибка 404:", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotFound(final ItemNotFoundException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Ошибка 404:", e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyExist(final UserAlreadyExist e) {
        log.error(e.getMessage());
        return new ErrorResponse("Ошибка 409:", e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final Exception e) {
        log.error(e.getMessage());
        return new ErrorResponse("Ошибка 500:", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handeDateTimeException(final DateTimeException e){
        log.error(e.getMessage());
        return new ErrorResponse("Ошибка 400:", e.getMessage());
    }
}
