package ru.yandex.practicum.kanban.repository.impls;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.Repository;

import java.util.*;

public class InMemoryTaskRepositoryImpl implements Repository<Task> {

    private Map<Integer, Task> tasks;

    public InMemoryTaskRepositoryImpl() {
        this.tasks = new HashMap<>();
    }

    @Override
    public List<Task> getAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAll() {
        tasks.clear();
    }

    @Override
    public Optional<Task> getById(int id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public void deleteById(int id) {
        tasks.remove(id);
    }

    @Override
    public void create(Task task) {
        tasks.put(task.getId(),task);
    }

    @Override
    public void update(Task task) {
        tasks.replace(task.getId(),task);
    }
}