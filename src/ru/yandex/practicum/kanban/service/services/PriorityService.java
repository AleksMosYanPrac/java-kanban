package ru.yandex.practicum.kanban.service.services;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.exceptions.PriorityManagerTimeIntersection;

import java.util.Set;

public interface PriorityService {

    Task add(Task task) throws PriorityManagerTimeIntersection;

    boolean hasTimeIntersection(Task task);

    Set<Task> sortByStarTime();

    void update(Task updatedTask) throws PriorityManagerTimeIntersection;

    void delete(Task deletedTask);
}