package ru.yandex.practicum.kanban.service.services;

import ru.yandex.practicum.kanban.model.Task;

import java.util.List;

public interface HistoryService {

    Task add(Task task);

    void remove(int id);

    List<Task> getHistory();
}