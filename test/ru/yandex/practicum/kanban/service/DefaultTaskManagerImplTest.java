package ru.yandex.practicum.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.HistoryRepository;
import ru.yandex.practicum.kanban.repository.Repository;
import ru.yandex.practicum.kanban.repository.impls.in_memory.InMemoryEpicRepositoryImpl;
import ru.yandex.practicum.kanban.repository.impls.in_memory.InMemoryHistoryRepositoryWithCopyAndLimitElements;
import ru.yandex.practicum.kanban.repository.impls.in_memory.InMemorySubtaskRepositoryImpl;
import ru.yandex.practicum.kanban.repository.impls.in_memory.InMemoryTaskRepositoryImpl;
import ru.yandex.practicum.kanban.service.managers.TaskManager;
import ru.yandex.practicum.kanban.service.managers.TaskManagerTest;
import ru.yandex.practicum.kanban.service.services.impls.HistoryServiceImpl;
import ru.yandex.practicum.kanban.service.services.impls.PriorityServiceImpl;
import ru.yandex.practicum.kanban.service.services.impls.RepositoryServiceImpl;
import ru.yandex.practicum.kanban.service.services.impls.TaskServiceImpl;
import ru.yandex.practicum.kanban.service.util.Managers;

public class DefaultTaskManagerImplTest extends TaskManagerTest<TaskManager> {

    protected Repository<Task> taskRepository = new InMemoryTaskRepositoryImpl();
    protected Repository<Epic> epicRepository = new InMemoryEpicRepositoryImpl();
    protected Repository<Subtask> subtaskRepository = new InMemorySubtaskRepositoryImpl();
    protected HistoryRepository historyRepository;

    @BeforeEach
    protected void setUp() {
        historyRepository = new InMemoryHistoryRepositoryWithCopyAndLimitElements();

        taskService = new TaskServiceImpl();
        repositoryService = new RepositoryServiceImpl(taskRepository, epicRepository, subtaskRepository);
        historyService = new HistoryServiceImpl(historyRepository);
        priorityService = new PriorityServiceImpl();

        taskManager = Managers.getDefault(taskRepository, subtaskRepository, epicRepository, historyRepository);
    }
}