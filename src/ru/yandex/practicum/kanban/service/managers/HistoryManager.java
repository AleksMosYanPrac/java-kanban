package ru.yandex.practicum.kanban.service.managers;

import ru.yandex.practicum.kanban.model.Task;

import java.util.List;

public interface HistoryManager extends TaskManager{
    List<Task> getHistoryOfViewedTasks();
}