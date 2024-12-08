package ru.yandex.practicum.kanban.repository;

import ru.yandex.practicum.kanban.model.Task;

import java.util.List;

public interface HistoryRepository {

    void addToList(Task task);

    void deleteFromList(int id);

    List<Task> listOfViewedTasks();
}