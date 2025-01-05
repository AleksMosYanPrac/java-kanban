package ru.yandex.practicum.kanban.service.exceptions;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}