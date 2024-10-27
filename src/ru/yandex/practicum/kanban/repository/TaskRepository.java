package ru.yandex.practicum.kanban.repository;

import ru.yandex.practicum.kanban.model.Task;

import java.util.*;

public class TaskRepository {

    private Map<Integer, Task> tasks;

    public TaskRepository() {
        this.tasks = new HashMap<>();
    }

    public List<Task> getAll() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAll() {
        tasks.clear();
    }

    public Optional<Task> getById(int id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public void deleteById(int id) {
        tasks.remove(id);
    }

    public void create(Task task) {
        tasks.put(task.getId(),task);
    }

    public void update(Task task) {
        tasks.replace(task.getId(),task);
    }
}
