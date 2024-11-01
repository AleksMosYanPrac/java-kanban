package ru.yandex.practicum.kanban.service.util;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.HistoryRepository;
import ru.yandex.practicum.kanban.repository.Repository;
import ru.yandex.practicum.kanban.repository.impls.InMemoryEpicRepositoryImpl;
import ru.yandex.practicum.kanban.repository.impls.InMemoryHistoryRepository;
import ru.yandex.practicum.kanban.repository.impls.InMemorySubtaskRepositoryImpl;
import ru.yandex.practicum.kanban.repository.impls.InMemoryTaskRepositoryImpl;
import ru.yandex.practicum.kanban.service.HistoryManager;
import ru.yandex.practicum.kanban.service.HistoryManagerImpl;
import ru.yandex.practicum.kanban.service.TaskManager;
import ru.yandex.practicum.kanban.service.TaskManagerImpl;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        Repository<Task> taskRepository = new InMemoryTaskRepositoryImpl();
        Repository<Epic> epicRepository = new InMemoryEpicRepositoryImpl();
        Repository<Subtask> subtaskRepository = new InMemorySubtaskRepositoryImpl();

        HistoryRepository historyRepository = new InMemoryHistoryRepository();
        HistoryManager historyManager = new HistoryManagerImpl(historyRepository);

        return new TaskManagerImpl(taskRepository, epicRepository, subtaskRepository, historyManager);
    }
}