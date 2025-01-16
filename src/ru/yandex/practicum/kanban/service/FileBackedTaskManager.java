package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskDTO;
import ru.yandex.practicum.kanban.repository.BackedRepository;
import ru.yandex.practicum.kanban.service.exceptions.ManagerReadException;
import ru.yandex.practicum.kanban.service.exceptions.ManagerSaveException;
import ru.yandex.practicum.kanban.service.services.impls.RepositoryServiceImpl;
import ru.yandex.practicum.kanban.service.services.impls.TaskServiceImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class FileBackedTaskManager extends TaskManagerImpl {

    private Path path;
    private List<BackedRepository> repositoryList;

    public FileBackedTaskManager(BackedRepository<Task> tasks,
                                 BackedRepository<Epic> epics,
                                 BackedRepository<Subtask> subtasks,
                                 Path path) {
        super(new TaskServiceImpl(),
                new RepositoryServiceImpl(tasks.getRepository(), epics.getRepository(), subtasks.getRepository()));
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
    public Task deleteTask(TaskDTO task) {
        Task deletedTask = super.deleteTask(task);
        saveData();
        return deletedTask;
    }

    @Override
    public Subtask deleteSubtask(TaskDTO subtask) {
        Subtask deletedeleteSubtask = super.deleteSubtask(subtask);
        saveData();
        return deletedeleteSubtask;
    }

    @Override
    public Epic deleteEpic(TaskDTO epic) {
        Epic deletedEpic = super.deleteEpic(epic);
        saveData();
        return deletedEpic;
    }

    @Override
    public Task updateTask(TaskDTO task) {
        Task updatedTask = super.updateTask(task);
        saveData();
        return updatedTask;
    }

    @Override
    public Subtask updateSubtask(TaskDTO subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        saveData();
        return updatedSubtask;
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