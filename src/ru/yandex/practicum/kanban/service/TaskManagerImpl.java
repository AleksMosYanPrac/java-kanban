package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskDTO;
import ru.yandex.practicum.kanban.repository.Repository;
import ru.yandex.practicum.kanban.service.util.IdGenerator;

import java.util.List;
import java.util.Objects;

public class TaskManagerImpl implements TaskManager {

    private final Repository<Task> taskRepository;
    private final Repository<Epic> epicRepository;
    private final Repository<Subtask> subtaskRepository;
    private final HistoryManager historyManager;

    public TaskManagerImpl(Repository<Task> tasks, Repository<Epic> epics, Repository<Subtask> subtasks, HistoryManager historyManager) {
        this.taskRepository = tasks;
        this.epicRepository = epics;
        this.subtaskRepository = subtasks;
        this.historyManager = historyManager;
    }

    @Override
    public Task createTask(TaskDTO task) {
        int id = IdGenerator.generate();
        Task newTask = new Task(id, task.getTitle(), task.getDescription(), task.getStatus());
        taskRepository.create(newTask);
        return newTask;
    }

    @Override
    public Subtask createSubtask(TaskDTO subtask) {
        int id = IdGenerator.generate();
        Subtask newSubtask = new Subtask(id, subtask.getTitle(), subtask.getDescription(), subtask.getStatus());
        subtaskRepository.create(newSubtask);
        return newSubtask;
    }

    @Override
    public Epic createEpic(TaskDTO epic) {
        int id = IdGenerator.generate();
        Epic newEpic = new Epic(id, epic.getTitle(), epic.getDescription(), epic.getStatus());
        epicRepository.create(newEpic);
        return newEpic;
    }

    @Override
    public Epic createEpic(TaskDTO epic, TaskDTO... subtasks) {
        Epic newEpic = createEpic(epic);
        for (TaskDTO subtask : subtasks) {
            Subtask newSubtask = createSubtask(subtask);
            linkSubtaskWithEpic(newEpic, newSubtask);
        }
        return newEpic;
    }

    @Override
    public List<Subtask> getSubtasksForEpic(TaskDTO epic) {
        Epic epicById = epicRepository.getById(epic.getId()).orElseThrow();
        return epicById.getSubtasks();
    }

    @Override
    public void deleteTask(TaskDTO task) {
        Task taskById = taskRepository.getById(task.getId()).orElseThrow();
        historyManager.remove(taskById.getId());
        taskRepository.deleteById(taskById.getId());
    }

    @Override
    public void deleteSubtask(TaskDTO subtask) {
        Subtask subtaskById = subtaskRepository.getById(subtask.getId()).orElseThrow();
        unlinkSubtaskFromEpic(subtaskById.getEpic(), subtaskById);
        historyManager.remove(subtaskById.getId());
        subtaskRepository.deleteById(subtask.getId());
    }

    @Override
    public void deleteEpic(TaskDTO epic) {
        Epic epicById = epicRepository.getById(epic.getId()).orElseThrow();
        List<Subtask> subtasks = epicById.getSubtasks();
        for (Subtask subtask : subtasks) {
            historyManager.remove(subtask.getId());
            subtaskRepository.deleteById(subtask.getId());
        }
        historyManager.remove(epicById.getId());
        epicRepository.deleteById(epicById.getId());
    }

    @Override
    public void updateTask(TaskDTO task) {
        int updatedTaskId = taskRepository.getById(task.getId()).orElseThrow().getId();
        Task updatedTask = new Task(updatedTaskId, task.getTitle(), task.getDescription(), task.getStatus());
        taskRepository.update(updatedTask);
    }

    @Override
    public void updateSubtask(TaskDTO subtask) {
        Subtask subtaskById = subtaskRepository.getById(subtask.getId()).orElseThrow();
        Subtask updatedSubtask = new Subtask(subtask.getId(), subtask.getTitle(), subtask.getDescription(), subtask.getStatus());
        if (Objects.nonNull(subtaskById.getEpic())) {
            Epic linkedEpic = epicRepository.getById(subtaskById.getEpic().getId()).orElseThrow();
            unlinkSubtaskFromEpic(linkedEpic, subtaskById);
            linkSubtaskWithEpic(linkedEpic, updatedSubtask);
            epicRepository.update(linkedEpic);
        }
        subtaskRepository.update(updatedSubtask);
    }

    @Override
    public Task getTaskById(int id) {
        Task task = taskRepository.getById(id).orElseThrow();
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubTaskById(int id) {
        Subtask subtask = subtaskRepository.getById(id).orElseThrow();
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epicRepository.getById(id).orElseThrow();
        historyManager.add(epic);
        return epic;
    }

    @Override
    public List<Task> getHistoryOfViewedTasks() {
        return historyManager.getHistory();
    }

    private void linkSubtaskWithEpic(Epic newEpic, Subtask subtask) {
        newEpic.addSubtask(subtask);
        subtask.addEpic(newEpic);
    }

    private void unlinkSubtaskFromEpic(Epic epic, Subtask subtask) {
        if (Objects.nonNull(epic)) {
            epic.deleteSubtask(subtask);
        }
    }
}