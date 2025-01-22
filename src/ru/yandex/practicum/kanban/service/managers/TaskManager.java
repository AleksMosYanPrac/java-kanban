package ru.yandex.practicum.kanban.service.managers;

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

    Task deleteTask(TaskDTO task);

    Subtask deleteSubtask(TaskDTO subtask);

    Epic deleteEpic(TaskDTO epic);

    Task updateTask(TaskDTO task);

    Subtask updateSubtask(TaskDTO subtask);

    Task getTaskById(int id);

    Subtask getSubTaskById(int id);

    Epic getEpicById(int id);
}