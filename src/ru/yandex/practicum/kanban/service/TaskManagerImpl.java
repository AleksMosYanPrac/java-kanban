package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskDTO;
import ru.yandex.practicum.kanban.service.managers.TaskManager;
import ru.yandex.practicum.kanban.service.services.RepositoryService;
import ru.yandex.practicum.kanban.service.services.TaskService;

import java.util.List;

public class TaskManagerImpl implements TaskManager {

    protected final TaskService taskService;
    protected final RepositoryService repositoryService;

    public TaskManagerImpl(TaskService taskService, RepositoryService repositoryService) {
        this.taskService = taskService;
        this.repositoryService = repositoryService;
    }

    @Override
    public Task createTask(TaskDTO task) {
        Task newTask = taskService.createTask(task);
        repositoryService.addTask(newTask);
        return newTask;
    }

    @Override
    public Subtask createSubtask(TaskDTO subtask) {
        Subtask newSubtask = taskService.createSubtask(subtask);
        repositoryService.addSubtask(newSubtask);
        return newSubtask;
    }

    @Override
    public Epic createEpic(TaskDTO epic) {
        Epic newEpic = taskService.createEpic(epic);
        repositoryService.addEpic(newEpic);
        return newEpic;
    }

    @Override
    public Epic createEpic(TaskDTO epic, TaskDTO... subtasks) {
        Epic newEpic = taskService.createEpic(epic, subtasks);
        repositoryService.addEpic(newEpic);
        return newEpic;
    }

    @Override
    public List<Subtask> getSubtasksForEpic(TaskDTO epic) {
        Epic epicById = repositoryService.getEpicById(epic.getId()).orElseThrow();
        return epicById.getSubtasks();
    }

    @Override
    public Task deleteTask(TaskDTO task) {
       return repositoryService.removeTask(task.getId());
    }

    @Override
    public Subtask deleteSubtask(TaskDTO subtask) {
        return repositoryService.removeSubtask(subtask.getId());
    }

    @Override
    public Epic deleteEpic(TaskDTO epic) {
       return repositoryService.removeEpic(epic.getId());
    }

    @Override
    public Task updateTask(TaskDTO task) {
        Task taskById = repositoryService.getTaskById(task.getId()).orElseThrow();
        Task updatedTask = taskService.createTask(task,taskById);
        repositoryService.updateTask(updatedTask);
        return updatedTask;
    }

    @Override
    public Subtask updateSubtask(TaskDTO subtask) {
        Subtask subtaskById = repositoryService.getSubtaskById(subtask.getId()).orElseThrow();
        Subtask updatedSubtask = taskService.createSubtask(subtask,subtaskById);
        repositoryService.updateSubtask(updatedSubtask);
        return updatedSubtask;
    }

    @Override
    public Task getTaskById(int id) {
        return repositoryService.getTaskById(id).orElseThrow();
    }

    @Override
    public Subtask getSubTaskById(int id) {
        return repositoryService.getSubtaskById(id).orElseThrow();
    }

    @Override
    public Epic getEpicById(int id) {
        return repositoryService.getEpicById(id).orElseThrow();
    }
}