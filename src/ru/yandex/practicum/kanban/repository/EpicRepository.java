package ru.yandex.practicum.kanban.repository;

import ru.yandex.practicum.kanban.model.Epic;

import java.util.*;

public class EpicRepository {

    private Map<Integer, Epic> epicTasks;

    public EpicRepository() {
        this.epicTasks = new HashMap<>();
    }

    public List<Epic> getAll() {
        return new ArrayList<>(epicTasks.values());
    }

    public void deleteAll() {
        epicTasks.clear();
    }

    public Optional<Epic> getById(int id) {
        return Optional.ofNullable(epicTasks.get(id));
    }

    public void deleteById(int id) {
        epicTasks.remove(id);
    }

    public void create(Epic epic) {
        epicTasks.put(epic.getId(), epic);
    }

    public void update(Epic epic) {
        epicTasks.replace(epic.getId(), epic);
    }
}
