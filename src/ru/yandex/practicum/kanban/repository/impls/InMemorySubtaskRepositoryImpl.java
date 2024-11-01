package ru.yandex.practicum.kanban.repository.impls;

import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.repository.Repository;

import java.util.*;

public class InMemorySubtaskRepositoryImpl implements Repository<Subtask> {

    private Map<Integer, Subtask> subTasks;

    public InMemorySubtaskRepositoryImpl() {
        this.subTasks = new HashMap<>();
    }

    public List<Subtask> getAll() {
        return new ArrayList<>(subTasks.values());
    }

    public void deleteAll() {
        subTasks.clear();
    }

    public Optional<Subtask> getById(int id) {
        return Optional.ofNullable(subTasks.get(id));
    }

    public void deleteById(int id) {
        subTasks.remove(id);
    }

    public void create(Subtask subTask) {
        subTasks.put(subTask.getId(), subTask);
    }

    public void update(Subtask subTask) {
        subTasks.replace(subTask.getId(), subTask);
    }
}