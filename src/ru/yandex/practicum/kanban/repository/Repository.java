package ru.yandex.practicum.kanban.repository;

import ru.yandex.practicum.kanban.model.Task;

import java.util.List;
import java.util.Optional;

public interface Repository<T extends Task> {

    List<T> getAll();

    void deleteAll();

    Optional<T> getById(int id);

    void deleteById(int id);

    void create(T t);

    void update(T t);
}
