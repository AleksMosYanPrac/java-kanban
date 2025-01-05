package ru.yandex.practicum.kanban.repository.impls.in_file.exceptions;

public class FileReadException extends RuntimeException {

    public FileReadException(String message, Throwable cause) {
        super(message, cause);
    }
}