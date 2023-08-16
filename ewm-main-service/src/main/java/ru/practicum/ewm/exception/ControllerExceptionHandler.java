package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.ewm.utils.constants.DateTimeFormat.DATE_TIME_FORMATTER;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseApiError methodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message;
        if (e.hasFieldErrors()) {
            StringBuilder builder = new StringBuilder();
            for (FieldError fieldError : e.getFieldErrors()) {
                builder.append("Field: ");
                builder.append(fieldError.getField());
                builder.append(". Error: ");
                builder.append(fieldError.getDefaultMessage());
                builder.append(". Value: ");
                builder.append(fieldError.getRejectedValue());
                builder.append(". ");
            }
            message = builder.toString();
        } else {
            message = e.getMessage();
        }

        log.error(e.getStackTrace()[0].getMethodName() + ": " + e.getMessage());
        return new ResponseApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                message,
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseApiError notFoundException(final NotFoundException e) {
        log.error(e.getStackTrace()[0].getMethodName() + ": " + e.getMessage());
        return new ResponseApiError(
                HttpStatus.NOT_FOUND,
                "The required object was not found.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseApiError constraintViolationException(ConstraintViolationException e) {
        log.error(e.getStackTrace()[0].getMethodName() + ": " + e.getMessage());
        return new ResponseApiError(
                HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(UnsupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseApiError unsupportedStateException(UnsupportedException e) {
        log.error(e.getStackTrace()[0].getMethodName() + ": " + e.getMessage());
        return new ResponseApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseApiError conflictException(ConflictException e) {
        log.error(e.getStackTrace()[0].getMethodName() + ": " + e.getMessage());
        return new ResponseApiError(
                HttpStatus.CONFLICT,
                "For the requested operation the conditions are not met.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseApiError dataIntegrityViolationException(final DataIntegrityViolationException e) {
        return new ResponseApiError(
                HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }
}
