package ru.yandex.practicum.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.Repository;
import ru.yandex.practicum.kanban.repository.impls.in_memory.InMemoryEpicRepositoryImpl;
import ru.yandex.practicum.kanban.repository.impls.in_memory.InMemorySubtaskRepositoryImpl;
import ru.yandex.practicum.kanban.repository.impls.in_memory.InMemoryTaskRepositoryImpl;
import ru.yandex.practicum.kanban.service.managers.TaskManagerTest;
import ru.yandex.practicum.kanban.service.services.impls.RepositoryServiceImpl;
import ru.yandex.practicum.kanban.service.services.impls.TaskServiceImpl;

public class DefaultTaskManagerImplTest extends TaskManagerTest<TaskManagerImpl> {

    private Repository<Task> taskRepository = new InMemoryTaskRepositoryImpl();
    private Repository<Epic> epicRepository = new InMemoryEpicRepositoryImpl();
    private Repository<Subtask> subtaskRepository = new InMemorySubtaskRepositoryImpl();

    @BeforeEach
    protected void setUp() {
        repositoryService = new RepositoryServiceImpl(taskRepository, epicRepository, subtaskRepository);
        taskManager = new TaskManagerImpl(new TaskServiceImpl(), repositoryService);
    }
}