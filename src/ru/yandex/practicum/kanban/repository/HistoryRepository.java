package ru.yandex.practicum.kanban.repository;

import ru.yandex.practicum.kanban.model.Task;

import java.util.List;

public interface HistoryRepository {

    void add(Task task);

    void delete(int id);

    List<Task> list();
}