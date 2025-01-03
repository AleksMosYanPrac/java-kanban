package ru.yandex.practicum.kanban.repository.impls.in_file.exceptions;

public class InFileReadException extends RuntimeException {

    public InFileReadException(String message, Throwable cause) {
        super(message, cause);
    }
}