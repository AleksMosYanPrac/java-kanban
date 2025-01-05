package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskDTO;
import ru.yandex.practicum.kanban.repository.BackedRepository;
import ru.yandex.practicum.kanban.service.exceptions.ManagerReadException;
import ru.yandex.practicum.kanban.service.exceptions.ManagerSaveException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends TaskManagerImpl {

    private Path path;
    private List<BackedRepository> repositoryList;

    public FileBackedTaskManager(BackedRepository<Task> tasks,
                                 BackedRepository<Epic> epics,
                                 BackedRepository<Subtask> subtasks,
                                 HistoryManager historyManager,
                                 Path path) {
        super(tasks.getRepository(), epics.getRepository(), subtasks.getRepository(), historyManager);
        this.path = path;
        this.repositoryList = new ArrayList<>();
        repositoryList.addAll(List.of(tasks, epics, subtasks));
        readData();
    }

    @Override
    public Task createTask(TaskDTO task) {
        Task createdTask = super.createTask(task);
        saveData();
        return createdTask;
    }

    @Override
    public Subtask createSubtask(TaskDTO subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        saveData();
        return createdSubtask;
    }

    @Override
    public Epic createEpic(TaskDTO epic) {
        Epic createdEpic = super.createEpic(epic);
        saveData();
        return createdEpic;
    }

    @Override
    public Epic createEpic(TaskDTO epic, TaskDTO... subtasks) {
        Epic createdEpic = super.createEpic(epic, subtasks);
        saveData();
        return createdEpic;
    }

    @Override
    public List<Subtask> getSubtasksForEpic(TaskDTO epic) {
        return super.getSubtasksForEpic(epic);
    }

    @Override
    public void deleteTask(TaskDTO task) {
        super.deleteTask(task);
        saveData();
    }

    @Override
    public void deleteSubtask(TaskDTO subtask) {
        super.deleteSubtask(subtask);
        saveData();
    }

    @Override
    public void deleteEpic(TaskDTO epic) {
        super.deleteEpic(epic);
        saveData();
    }

    @Override
    public void updateTask(TaskDTO task) {
        super.updateTask(task);
        saveData();
    }

    @Override
    public void updateSubtask(TaskDTO subtask) {
        super.updateSubtask(subtask);
        saveData();
    }

    @Override
    public Task getTaskById(int id) {
        return super.getTaskById(id);
    }

    @Override
    public Subtask getSubTaskById(int id) {
        return super.getSubTaskById(id);
    }

    @Override
    public Epic getEpicById(int id) {
        return super.getEpicById(id);
    }

    @Override
    public List<Task> getHistoryOfViewedTasks() {
        return super.getHistoryOfViewedTasks();
    }

    private void readData() {
        repositoryList.forEach(repository -> {
            try {
                repository.readData(path);
            } catch (IOException e) {
                throw new ManagerReadException("can't read data from:" + path, e);
            }
        });
    }

    private void saveData() {
        repositoryList.forEach(repository -> {
            try {
                repository.saveData(path);
            } catch (IOException e) {
                throw new ManagerSaveException("can't write data to:" + path, e);
            }
        });
    }
}