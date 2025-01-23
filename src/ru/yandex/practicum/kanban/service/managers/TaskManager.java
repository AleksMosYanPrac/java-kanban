package ru.yandex.practicum.kanban.service.managers;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskDTO;

import java.util.List;

public interface TaskManager {

    Task createTask(TaskDTO task) throws Exception;

    Subtask createSubtask(TaskDTO subtask) throws Exception;

    Epic createEpic(TaskDTO epic) throws Exception;

    Epic createEpic(TaskDTO epic, TaskDTO... subtasks) throws Exception;

    List<Subtask> getSubtasksForEpic(TaskDTO epic);

    Task deleteTask(TaskDTO task);

    Subtask deleteSubtask(TaskDTO subtask);

    Epic deleteEpic(TaskDTO epic);

    Task updateTask(TaskDTO task) throws Exception;

    Subtask updateSubtask(TaskDTO subtask) throws Exception;

    Task getTaskById(int id);

    Subtask getSubTaskById(int id);

    Epic getEpicById(int id);
}