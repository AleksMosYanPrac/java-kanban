package ru.yandex.practicum.kanban.repository.impls.in_file.exceptions;

public class FileWriteException extends RuntimeException {

    public FileWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
