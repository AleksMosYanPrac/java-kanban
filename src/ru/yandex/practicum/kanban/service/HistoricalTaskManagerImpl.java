package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskDTO;
import ru.yandex.practicum.kanban.service.managers.HistoryManager;
import ru.yandex.practicum.kanban.service.services.HistoryService;
import ru.yandex.practicum.kanban.service.services.RepositoryService;
import ru.yandex.practicum.kanban.service.services.TaskService;

import java.util.List;

public class HistoricalTaskManagerImpl extends TaskManagerImpl implements HistoryManager {

    private final HistoryService historyService;

    public HistoricalTaskManagerImpl(TaskService taskService,
                                     RepositoryService repositoryService,
                                     HistoryService historyService) {
        super(taskService, repositoryService);
        this.historyService = historyService;
    }

    @Override
    public Task deleteTask(TaskDTO task) {
        Task deletedTask = super.deleteTask(task);
        historyService.remove(deletedTask.getId());
        return deletedTask;
    }

    @Override
    public Subtask deleteSubtask(TaskDTO subtask) {
        Subtask deletedSubtask = super.deleteSubtask(subtask);
        historyService.remove(deletedSubtask.getId());
        return deletedSubtask;
    }

    @Override
    public Epic deleteEpic(TaskDTO epic) {
        Epic deletedEpic = super.deleteEpic(epic);
        historyService.remove(deletedEpic.getId());
        return deletedEpic;
    }

    @Override
    public Task getTaskById(int id) {
        Task taskById = super.getTaskById(id);
        historyService.add(taskById);
        return taskById;
    }

    @Override
    public Subtask getSubTaskById(int id) {
        Subtask subtaskById = super.getSubTaskById(id);
        historyService.add(subtaskById);
        return subtaskById;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epicById = super.getEpicById(id);
        historyService.add(epicById);
        return epicById;
    }

    @Override
    public List<Task> getHistoryOfViewedTasks() {
        return historyService.getHistory();
    }
}