package ru.yandex.practicum.kanban.service.managers;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskDTO;
import ru.yandex.practicum.kanban.service.exceptions.PriorityManagerTimeIntersection;

import java.util.List;

public interface TaskManager extends HistoryManager, PriorityManager {

    Task createTask(TaskDTO task) throws PriorityManagerTimeIntersection;

    Subtask createSubtask(TaskDTO subtask) throws PriorityManagerTimeIntersection;

    Epic createEpic(TaskDTO epic) throws PriorityManagerTimeIntersection;

    Epic createEpic(TaskDTO epic, TaskDTO... subtasks) throws PriorityManagerTimeIntersection;

    List<Subtask> getSubtasksForEpic(int id);

    Task deleteTask(int id);

    Subtask deleteSubtask(int id);

    Epic deleteEpic(int id);

    Task updateTask(TaskDTO task) throws PriorityManagerTimeIntersection;

    Subtask updateSubtask(TaskDTO subtask) throws PriorityManagerTimeIntersection;

    Task getTaskById(int id);

    Subtask getSubTaskById(int id);

    Epic getEpicById(int id);

    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();
}