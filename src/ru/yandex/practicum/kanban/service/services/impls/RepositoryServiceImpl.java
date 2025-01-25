package ru.yandex.practicum.kanban.service.services.impls;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.Repository;
import ru.yandex.practicum.kanban.service.services.RepositoryService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class RepositoryServiceImpl implements RepositoryService {

    private final Repository<Task> taskRepository;
    private final Repository<Epic> epicRepository;
    private final Repository<Subtask> subtaskRepository;

    public RepositoryServiceImpl(Repository<Task> taskRepository,
                                 Repository<Epic> epicRepository,
                                 Repository<Subtask> subtaskRepository) {
        this.taskRepository = taskRepository;
        this.epicRepository = epicRepository;
        this.subtaskRepository = subtaskRepository;
    }

    @Override
    public void addTask(Task task) {
        taskRepository.create(task);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        subtaskRepository.create(subtask);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.getSubtasks().forEach(this::addSubtask);
        epicRepository.create(epic);
    }

    @Override
    public Optional<Task> getTaskById(int id) {
        return taskRepository.getById(id);
    }

    @Override
    public Optional<Subtask> getSubtaskById(int id) {
        return subtaskRepository.getById(id);
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        return epicRepository.getById(id);
    }

    @Override
    public Task removeTask(int id) {
        Task task = taskRepository.getById(id).orElseThrow();
        taskRepository.deleteById(id);
        return task;
    }

    @Override
    public Subtask removeSubtask(int id) throws NoSuchElementException {
        Subtask subtaskById = subtaskRepository.getById(id).orElseThrow();
        if (subtaskById.hasEpic()) {
            Epic epic = getEpicById(subtaskById.getEpic().getId()).orElseThrow();
            epic.deleteSubtask(subtaskById);
            epicRepository.update(epic);
        }
        subtaskRepository.deleteById(subtaskById.getId());
        return subtaskById;
    }

    @Override
    public Epic removeEpic(int id) {
        Epic epicById = epicRepository.getById(id).orElseThrow();
        epicById.getSubtasks().forEach(s -> subtaskRepository.deleteById(s.getId()));
        epicRepository.deleteById(epicById.getId());
        return epicById;
    }

    @Override
    public void updateTask(Task updatedTask) {
        taskRepository.update(updatedTask);
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        if (updatedSubtask.hasEpic()) {
            Epic linkedEpic = epicRepository.getById(updatedSubtask.getEpic().getId()).orElseThrow();
            Subtask oldSubtask = subtaskRepository.getById(updatedSubtask.getId()).orElseThrow();
            linkedEpic.deleteSubtask(oldSubtask);
            linkedEpic.addSubtask(updatedSubtask);
            epicRepository.update(linkedEpic);
        }
        subtaskRepository.update(updatedSubtask);
    }

    @Override
    public List<Epic> getAllEpic() {
        return epicRepository.getAll();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return subtaskRepository.getAll();
    }

    @Override
    public List<Task> getAllTask() {
        return taskRepository.getAll();
    }
}