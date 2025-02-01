package ru.yandex.practicum.kanban.service.managers;

import ru.yandex.practicum.kanban.model.Task;

import java.util.Set;

public interface PriorityManager {

    Set<Task> getPrioritizedTasks();
}