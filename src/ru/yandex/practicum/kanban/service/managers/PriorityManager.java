package ru.yandex.practicum.kanban.service.managers;

import ru.yandex.practicum.kanban.model.Task;

import java.util.TreeSet;

public interface PriorityManager extends TaskManager {

    TreeSet<Task> getPrioritizedTasks();
}