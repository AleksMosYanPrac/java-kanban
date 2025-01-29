package ru.yandex.practicum.kanban.http_api;

import ru.yandex.practicum.kanban.model.TaskDTO;

import java.util.List;

public interface RequestConverter {
    TaskDTO convertToObject(String requestBody);

    List<TaskDTO> convertToList(String requestBody);
}