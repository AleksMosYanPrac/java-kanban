package ru.yandex.practicum.kanban.service.services;

import ru.yandex.practicum.kanban.model.Task;

import java.util.Set;

public interface PriorityService {
    Task add(Task task);

    boolean hasTimeIntersection(Task task);

    Set<Task> sortByStarTime();

    void update(Task updatedTask);

    void delete(Task deletedTask);
}