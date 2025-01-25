package ru.yandex.practicum.kanban.service.util;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.BackedRepository;
import ru.yandex.practicum.kanban.repository.HistoryRepository;
import ru.yandex.practicum.kanban.repository.Repository;
import ru.yandex.practicum.kanban.repository.impls.in_file.FileDataSource;
import ru.yandex.practicum.kanban.repository.impls.in_file.InFileEpicRepositoryImpl;
import ru.yandex.practicum.kanban.repository.impls.in_file.InFileSubtaskRepositoryImpl;
import ru.yandex.practicum.kanban.repository.impls.in_file.InFileTaskRepositoryImpl;
import ru.yandex.practicum.kanban.repository.impls.in_file.datasource.CSVFileDataSource;
import ru.yandex.practicum.kanban.repository.impls.in_memory.*;
import ru.yandex.practicum.kanban.service.*;
import ru.yandex.practicum.kanban.service.managers.PriorityManager;
import ru.yandex.practicum.kanban.service.managers.TaskManager;
import ru.yandex.practicum.kanban.service.services.*;
import ru.yandex.practicum.kanban.service.services.impls.HistoryServiceImpl;
import ru.yandex.practicum.kanban.service.services.impls.PriorityServiceImpl;
import ru.yandex.practicum.kanban.service.services.impls.RepositoryServiceImpl;
import ru.yandex.practicum.kanban.service.services.impls.TaskServiceImpl;

import java.nio.file.Path;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        Repository<Task> taskRepository = new InMemoryTaskRepositoryImpl();
        Repository<Epic> epicRepository = new InMemoryEpicRepositoryImpl();
        Repository<Subtask> subtaskRepository = new InMemorySubtaskRepositoryImpl();

        TaskService taskService = new TaskServiceImpl();
        RepositoryService repositoryService =
                new RepositoryServiceImpl(taskRepository, epicRepository, subtaskRepository);

        return new TaskManagerImpl(taskService, repositoryService);
    }

    public static TaskManager getDefault(Repository<Task> taskRepository,
                                         Repository<Subtask> subtaskRepository,
                                         Repository<Epic> epicRepository) {

        TaskService taskService = new TaskServiceImpl();
        RepositoryService repositoryService =
                new RepositoryServiceImpl(taskRepository, epicRepository, subtaskRepository);

        return new TaskManagerImpl(taskService, repositoryService);
    }

    public static TaskManager fileStoredTaskManager(Path pathToFile) {

        FileDataSource dataSource = createDataSource(pathToFile);

        Repository<Task> taskRepository = new InFileTaskRepositoryImpl(dataSource);
        Repository<Epic> epicRepository = new InFileEpicRepositoryImpl(dataSource);
        Repository<Subtask> subtaskRepository = new InFileSubtaskRepositoryImpl(dataSource);

        TaskService taskService = new TaskServiceImpl();
        RepositoryService repositoryService =
                new RepositoryServiceImpl(taskRepository, epicRepository, subtaskRepository);

        return new TaskManagerImpl(taskService, repositoryService);
    }

    public static TaskManager fileBackedTaskManager(Path pathToFile) {
        BackedRepository<Task> taskRepository = new InMemoryTaskRepositoryWithBackupData();
        BackedRepository<Epic> epicRepository = new InMemoryEpicRepositoryWithBackupData();
        BackedRepository<Subtask> subtaskRepository = new InMemorySubtaskRepositoryWithBackupData();

        return new FileBackedTaskManager(taskRepository, epicRepository, subtaskRepository, pathToFile);
    }

    public static PriorityManager prioritizedHistoricalTaskManager() {
        Repository<Task> taskRepository = new InMemoryTaskRepositoryImpl();
        Repository<Epic> epicRepository = new InMemoryEpicRepositoryImpl();
        Repository<Subtask> subtaskRepository = new InMemorySubtaskRepositoryImpl();

        TaskService taskService = new TaskServiceImpl();
        RepositoryService repositoryService = createDefaultRepositoryService();
        HistoryService historyService = new HistoryServiceImpl(createDefaultHistoryRepository());
        PriorityService priorityService = new PriorityServiceImpl();

        return new PrioritizedHistoricalTaskManagerImpl(
                taskService,
                repositoryService,
                historyService,
                priorityService
        );
    }

    private static RepositoryService createDefaultRepositoryService() {
        Repository<Task> taskRepository = new InMemoryTaskRepositoryImpl();
        Repository<Epic> epicRepository = new InMemoryEpicRepositoryImpl();
        Repository<Subtask> subtaskRepository = new InMemorySubtaskRepositoryImpl();

        return new RepositoryServiceImpl(taskRepository, epicRepository, subtaskRepository);
    }

    private static HistoryRepository createDefaultHistoryRepository() {
        return new InMemoryHistoryRepositoryWithCopyAndLimitElements();
    }

    private static FileDataSource createDataSource(Path pathToFile) {
        return new CSVFileDataSource(pathToFile);
    }
}