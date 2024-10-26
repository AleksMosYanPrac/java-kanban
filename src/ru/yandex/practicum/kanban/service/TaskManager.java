package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskDTO;
import ru.yandex.practicum.kanban.repository.EpicRepository;
import ru.yandex.practicum.kanban.repository.SubtaskRepository;
import ru.yandex.practicum.kanban.repository.TaskRepository;
import ru.yandex.practicum.kanban.service.util.IdGenerator;

import java.util.List;
import java.util.Objects;

public class TaskManager {

    private TaskRepository taskRepository;
    private EpicRepository epicRepository;
    private SubtaskRepository subtaskRepository;

    public TaskManager(TaskRepository tasks, EpicRepository epics, SubtaskRepository subtasks) {
        this.taskRepository = tasks;
        this.epicRepository = epics;
        this.subtaskRepository = subtasks;
    }

    public Task createTask(TaskDTO task) {
        int id = IdGenerator.generate();
        Task newTask = new Task(id, task.getTitle(), task.getDescription(), task.getStatus());
        taskRepository.create(newTask);
        return newTask;
    }

    public Subtask createSubtask(TaskDTO subtask) {
        int id = IdGenerator.generate();
        Subtask newSubtask = new Subtask(id, subtask.getTitle(), subtask.getDescription(), subtask.getStatus());
        subtaskRepository.create(newSubtask);
        return newSubtask;
    }

    public Epic createEpic(TaskDTO epic) {
        int id = IdGenerator.generate();
        Epic newEpic = new Epic(id, epic.getTitle(), epic.getDescription(), epic.getStatus());
        epicRepository.create(newEpic);
        return newEpic;
    }

    public Epic createEpic(TaskDTO epic, TaskDTO... subtasks) {
        Epic newEpic = createEpic(epic);
        for (TaskDTO subtask : subtasks) {
            Subtask newSubtask = createSubtask(subtask);
            linkSubtaskWithEpic(newEpic, newSubtask);
        }
        return newEpic;
    }

    public List<Subtask> getSubtasksForEpic(TaskDTO epic) {
        Epic epicById = epicRepository.getById(epic.getId()).orElseThrow();
        return epicById.getSubtasks();
    }

    public void deleteTask(TaskDTO task) {
        Task taskById = taskRepository.getById(task.getId()).orElseThrow();
        taskRepository.deleteById(taskById.getId());
    }

    public void deleteSubtask(TaskDTO subtask) {
        Subtask subtaskById = subtaskRepository.getById(subtask.getId()).orElseThrow();
        unlinkSubtaskFromEpic(subtaskById.getEpic(), subtaskById);
        subtaskRepository.deleteById(subtask.getId());
    }

    public void deleteEpic(TaskDTO epic) {
        Epic epicById = epicRepository.getById(epic.getId()).orElseThrow();
        List<Subtask> subtasks = epicById.getSubtasks();
        for (Subtask subtask : subtasks) {
            subtaskRepository.deleteById(subtask.getId());
        }
        epicRepository.deleteById(epicById.getId());
    }

    public void updateTask(TaskDTO task) {
        int updatedTaskId = taskRepository.getById(task.getId()).orElseThrow().getId();
        Task updatedTask = new Task(updatedTaskId, task.getTitle(), task.getDescription(), task.getStatus());
        taskRepository.update(updatedTask);
    }

    public void updateSubtask(TaskDTO subtask) {
        Subtask subtaskById = subtaskRepository.getById(subtask.getId()).orElseThrow();
        Subtask updatedSubtask = new Subtask(
                subtask.getId(), subtask.getTitle(), subtask.getDescription(), subtask.getStatus());
        if (Objects.nonNull(subtaskById.getEpic())) {
            Epic linkedEpic = epicRepository.getById(subtaskById.getEpic().getId()).orElseThrow();
            unlinkSubtaskFromEpic(linkedEpic, subtaskById);
            linkSubtaskWithEpic(linkedEpic, updatedSubtask);
            epicRepository.update(linkedEpic);
        }
        subtaskRepository.update(updatedSubtask);
    }

    private void linkSubtaskWithEpic(Epic newEpic, Subtask subtask) {
        newEpic.addSubtask(subtask);
        subtask.addEpic(newEpic);
    }

    private void unlinkSubtaskFromEpic(Epic epic, Subtask subtask) {
        epic.deleteSubtask(subtask);
    }

}