package ru.practicum.ewm.exception;

public class NotFoundException extends RuntimeException {
    private static final String message = "%s with id=%d was not found";

    public NotFoundException(String s) {
        super(s);
    }

    public NotFoundException(String entity, long categoryId) {
        super(
                String.format(message, entity, categoryId)
        );
    }
}
