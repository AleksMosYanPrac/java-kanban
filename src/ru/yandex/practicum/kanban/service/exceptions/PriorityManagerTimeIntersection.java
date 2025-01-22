package ru.yandex.practicum.kanban.service.exceptions;

public class PriorityManagerTimeIntersection extends RuntimeException {
    public PriorityManagerTimeIntersection(String message) {
        super(message);
    }
}