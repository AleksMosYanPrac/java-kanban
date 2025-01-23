package ru.yandex.practicum.kanban.service.exceptions;

public class PriorityManagerTimeIntersection extends Exception {
    public PriorityManagerTimeIntersection(String message) {
        super(message);
    }
}