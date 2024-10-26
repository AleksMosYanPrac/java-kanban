package ru.yandex.practicum.kanban.repository;

import ru.yandex.practicum.kanban.model.Subtask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SubtaskRepository {

    private Map<Integer, Subtask> subTasks;

    public SubtaskRepository() {
        this.subTasks = new HashMap<>();
    }

    public List<Subtask> getAll() {
        return subTasks.values().stream().toList();
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
