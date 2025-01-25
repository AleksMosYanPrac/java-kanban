package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskDTO;
import ru.yandex.practicum.kanban.service.exceptions.PriorityManagerTimeIntersection;
import ru.yandex.practicum.kanban.service.managers.PriorityManager;
import ru.yandex.practicum.kanban.service.services.HistoryService;
import ru.yandex.practicum.kanban.service.services.PriorityService;
import ru.yandex.practicum.kanban.service.services.RepositoryService;
import ru.yandex.practicum.kanban.service.services.TaskService;

import java.util.Set;

public final class PrioritizedHistoricalTaskManagerImpl extends HistoricalTaskManagerImpl implements PriorityManager {

    private final PriorityService priorityService;

    public PrioritizedHistoricalTaskManagerImpl(TaskService taskService,
                                                RepositoryService repositoryService,
                                                HistoryService historyService,
                                                PriorityService priorityService) {
        super(taskService, repositoryService, historyService);
        this.priorityService = priorityService;
    }

    @Override
    public Task createTask(TaskDTO task) throws PriorityManagerTimeIntersection {
        Task newTask = taskService.createTask(task);
        checkTimeIntersection(task, newTask);
        repositoryService.addTask(newTask);
        priorityService.add(newTask);
        return newTask;
    }

    @Override
    public Subtask createSubtask(TaskDTO subtask) throws PriorityManagerTimeIntersection {
        Subtask newSubtask = taskService.createSubtask(subtask);
        checkTimeIntersection(subtask, newSubtask);
        repositoryService.addSubtask(newSubtask);
        priorityService.add(newSubtask);
        return newSubtask;
    }

    @Override
    public Epic createEpic(TaskDTO epic) throws Exception {
        Epic newEpic = taskService.createEpic(epic);
        checkTimeIntersection(epic, newEpic);
        repositoryService.addEpic(newEpic);
        return newEpic;
    }

    @Override
    public Epic createEpic(TaskDTO epic, TaskDTO... subtasks) throws PriorityManagerTimeIntersection {
        Epic newEpic = taskService.createEpic(epic, subtasks);
        if (newEpic.getSubtasks().stream().anyMatch(priorityService::hasTimeIntersection)) {
            throw new PriorityManagerTimeIntersection("One or more subtasks has time intersection with another Tasks");
        } else {
            for (Subtask subtask : newEpic.getSubtasks()) {
                priorityService.add(subtask);
            }
        }
        repositoryService.addEpic(newEpic);
        return newEpic;
    }

    @Override
    public Task updateTask(TaskDTO task) throws PriorityManagerTimeIntersection {
        Task taskById = repositoryService.getTaskById(task.getId()).orElseThrow();
        Task updatedTask = taskService.createTask(task, taskById);
        checkTimeIntersection(task, updatedTask);
        priorityService.update(updatedTask);
        repositoryService.updateTask(updatedTask);
        return updatedTask;
    }

    @Override
    public Subtask updateSubtask(TaskDTO subtask) throws PriorityManagerTimeIntersection {
        Subtask subtaskById = repositoryService.getSubtaskById(subtask.getId()).orElseThrow();
        Subtask updatedSubtask = taskService.createSubtask(subtask, subtaskById);
        checkTimeIntersection(subtask, updatedSubtask);
        priorityService.update(updatedSubtask);
        repositoryService.updateSubtask(updatedSubtask);
        return updatedSubtask;
    }

    @Override
    public Task deleteTask(TaskDTO task) {
        Task deletedTask = super.deleteTask(task);
        priorityService.delete(deletedTask);
        return deletedTask;
    }

    @Override
    public Subtask deleteSubtask(TaskDTO subtask) {
        Subtask deletedSubtask = super.deleteSubtask(subtask);
        priorityService.delete(deletedSubtask);
        return deletedSubtask;
    }

    @Override
    public Epic deleteEpic(TaskDTO epic) {
        Epic deletedEpic = super.deleteEpic(epic);
        deletedEpic.getSubtasks().forEach(priorityService::delete);
        return deletedEpic;
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return priorityService.sortByStarTime();
    }

    private void checkTimeIntersection(TaskDTO task, Task createdTask) throws PriorityManagerTimeIntersection {
        if (priorityService.hasTimeIntersection(createdTask)) {
            throw new PriorityManagerTimeIntersection(
                    task.getStartTime() + " and duration:" +
                            task.getDurationInMinutes() + "has time intersection with another Tasks");
        }
    }
}