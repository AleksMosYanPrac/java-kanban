package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskDTO;
import ru.yandex.practicum.kanban.service.exceptions.PriorityManagerTimeIntersection;
import ru.yandex.practicum.kanban.service.managers.TaskManager;
import ru.yandex.practicum.kanban.service.services.HistoryService;
import ru.yandex.practicum.kanban.service.services.PriorityService;
import ru.yandex.practicum.kanban.service.services.RepositoryService;
import ru.yandex.practicum.kanban.service.services.TaskService;

import java.util.List;
import java.util.Set;

public class TaskManagerImpl implements TaskManager {

    protected final TaskService taskService;
    protected final RepositoryService repositoryService;
    private final PriorityService priorityService;
    private final HistoryService historyService;

    public TaskManagerImpl(TaskService taskService,
                           RepositoryService repositoryService,
                           HistoryService historyService,
                           PriorityService priorityService) {
        this.taskService = taskService;
        this.repositoryService = repositoryService;
        this.priorityService = priorityService;
        this.historyService = historyService;
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
    public Epic createEpic(TaskDTO epic) throws PriorityManagerTimeIntersection {
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
        }
        for (Subtask subtask : newEpic.getSubtasks()) {
            priorityService.add(subtask);
        }
        repositoryService.addEpic(newEpic);
        return newEpic;
    }

    @Override
    public List<Subtask> getSubtasksForEpic(int id) {
        Epic epicById = repositoryService.getEpicById(id).orElseThrow();
        return epicById.getSubtasks();
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
    public Task getTaskById(int id) {
        Task taskById = repositoryService.getTaskById(id).orElseThrow();
        historyService.add(taskById);
        return taskById;
    }

    @Override
    public Subtask getSubTaskById(int id) {
        Subtask subtaskById = repositoryService.getSubtaskById(id).orElseThrow();
        historyService.add(subtaskById);
        return subtaskById;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epicById = repositoryService.getEpicById(id).orElseThrow();
        historyService.add(epicById);
        return epicById;
    }

    @Override
    public Task deleteTask(int id) {
        Task taskById = repositoryService.getTaskById(id).orElseThrow();
        Task deletedTask = repositoryService.removeTask(taskById.getId());
        historyService.remove(deletedTask.getId());
        priorityService.delete(deletedTask);
        return deletedTask;
    }

    @Override
    public Subtask deleteSubtask(int id) {
        Subtask subtaskById = repositoryService.getSubtaskById(id).orElseThrow();
        Subtask deletedSubtask = repositoryService.removeSubtask(subtaskById.getId());
        historyService.remove(deletedSubtask.getId());
        priorityService.delete(deletedSubtask);
        return deletedSubtask;
    }

    @Override
    public Epic deleteEpic(int id) {
        Epic epicById = repositoryService.getEpicById(id).orElseThrow();
        Epic deletedEpic = repositoryService.removeEpic(epicById.getId());
        historyService.remove(deletedEpic.getId());
        deletedEpic.getSubtasks().forEach(s -> historyService.remove(s.getId()));
        deletedEpic.getSubtasks().forEach(priorityService::delete);
        return deletedEpic;
    }

    @Override
    public List<Task> getAllTasks() {
        return repositoryService.getAllTask();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return repositoryService.getAllSubtasks();
    }

    @Override
    public List<Epic> getAllEpics() {
        return repositoryService.getAllEpic();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return priorityService.sortByStarTime();
    }

    @Override
    public List<Task> getHistoryOfViewedTasks() {
        return historyService.getHistory();
    }

    private void checkTimeIntersection(TaskDTO task, Task createdTask) throws PriorityManagerTimeIntersection {
        if (priorityService.hasTimeIntersection(createdTask)) {
            throw new PriorityManagerTimeIntersection(
                    task.getStartTime() + " and duration:" +
                            task.getDurationInMinutes() + "has time intersection with another Tasks");
        }
    }
}