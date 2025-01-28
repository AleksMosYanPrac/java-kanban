package ru.yandex.practicum.kanban.http_api;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;

import java.util.Collection;

public interface ResponseConverter {

    String convert(Task task);

    String convert(Collection<? extends Task> allTasks);

    String convert(Subtask task);

    String convert(Epic task);
}