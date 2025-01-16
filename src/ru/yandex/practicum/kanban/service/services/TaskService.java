package ru.yandex.practicum.kanban.service.services;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskDTO;

public interface TaskService {
    Task createTask(TaskDTO task);

    Subtask createSubtask(TaskDTO subtask);

    Epic createEpic(TaskDTO epic);

    Epic createEpic(TaskDTO epic, TaskDTO... subtasks);

    Task createTask(TaskDTO task, Task taskById);

    Subtask createSubtask(TaskDTO subtask, Subtask subtaskById);
}
