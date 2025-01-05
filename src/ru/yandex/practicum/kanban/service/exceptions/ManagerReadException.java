package ru.yandex.practicum.kanban.service.exceptions;

public class ManagerReadException extends RuntimeException {
    public ManagerReadException(String message, Throwable cause) {
        super(message, cause);
    }
}