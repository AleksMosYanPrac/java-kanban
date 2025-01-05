package ru.yandex.practicum.kanban.repository;

import ru.yandex.practicum.kanban.model.Task;

import java.io.IOException;
import java.nio.file.Path;

public interface BackedRepository<T extends Task> {

    Repository<T> getRepository();

    void readData(Path path) throws IOException;

    void saveData(Path path) throws IOException;
}