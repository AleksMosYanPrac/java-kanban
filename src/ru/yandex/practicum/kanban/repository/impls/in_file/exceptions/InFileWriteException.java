package ru.yandex.practicum.kanban.repository.impls.in_file.exceptions;

public class InFileWriteException extends RuntimeException {

    public InFileWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
