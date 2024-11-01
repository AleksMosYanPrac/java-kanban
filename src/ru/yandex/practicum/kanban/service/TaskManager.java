package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskDTO;

import java.util.List;

public interface TaskManager {
    Task createTask(TaskDTO task);

    Subtask createSubtask(TaskDTO subtask);

    Epic createEpic(TaskDTO epic);

    Epic createEpic(TaskDTO epic, TaskDTO... subtasks);

    List<Subtask> getSubtasksForEpic(TaskDTO epic);

    void deleteTask(TaskDTO task);

    void deleteSubtask(TaskDTO subtask);

    void deleteEpic(TaskDTO epic);

    void updateTask(TaskDTO task);

    void updateSubtask(TaskDTO subtask);

    Task getTaskById(int id);

    Subtask getSubTaskById(int id);

    Epic getEpicById(int id);

    List<Task> getHistoryOfViewedTasks();
}