package ru.yandex.practicum.kanban.service.services;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;

import java.util.List;
import java.util.Optional;

public interface RepositoryService {

    void addTask(Task task);

    void addSubtask(Subtask subtask);

    void addEpic(Epic epic);

    Optional<Epic> getEpicById(int id);

    Optional<Task> getTaskById(int id);

    Optional<Subtask> getSubtaskById(int id);

    Task removeTask(int id);

    Subtask removeSubtask(int id);

    Epic removeEpic(int id);

    void updateTask(Task updatedTask);

    void updateSubtask(Subtask updatedSubtask);

    List<Epic> getAllEpic();

    List<Subtask> getAllSubtasks();

    List<Task> getAllTask();
}