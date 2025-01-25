package ru.yandex.practicum.kanban.service.managers;

import ru.yandex.practicum.kanban.model.Task;

import java.util.Set;

public interface PriorityManager extends TaskManager {

    Set<Task> getPrioritizedTasks();
}