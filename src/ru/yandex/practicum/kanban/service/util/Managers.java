package ru.yandex.practicum.kanban.service.util;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.HistoryRepository;
import ru.yandex.practicum.kanban.repository.Repository;
import ru.yandex.practicum.kanban.repository.impls.in_file.FileDataSource;
import ru.yandex.practicum.kanban.repository.impls.in_file.InFileEpicRepositoryImpl;
import ru.yandex.practicum.kanban.repository.impls.in_file.InFileSubtaskRepositoryImpl;
import ru.yandex.practicum.kanban.repository.impls.in_file.InFileTaskRepositoryImpl;
import ru.yandex.practicum.kanban.repository.impls.in_file.datasource.CSVFileDataSource;
import ru.yandex.practicum.kanban.repository.impls.in_memory.InMemoryEpicRepositoryImpl;
import ru.yandex.practicum.kanban.repository.impls.in_memory.InMemoryHistoryRepositoryWithCopyAndLimitElements;
import ru.yandex.practicum.kanban.repository.impls.in_memory.InMemorySubtaskRepositoryImpl;
import ru.yandex.practicum.kanban.repository.impls.in_memory.InMemoryTaskRepositoryImpl;
import ru.yandex.practicum.kanban.service.HistoryManager;
import ru.yandex.practicum.kanban.service.HistoryManagerImpl;
import ru.yandex.practicum.kanban.service.TaskManager;
import ru.yandex.practicum.kanban.service.TaskManagerImpl;

import java.nio.file.Path;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        Repository<Task> taskRepository = new InMemoryTaskRepositoryImpl();
        Repository<Epic> epicRepository = new InMemoryEpicRepositoryImpl();
        Repository<Subtask> subtaskRepository = new InMemorySubtaskRepositoryImpl();

        HistoryRepository historyRepository = createDefaultHistoryRepository();
        HistoryManager historyManager = new HistoryManagerImpl(historyRepository);

        return new TaskManagerImpl(taskRepository, epicRepository, subtaskRepository, historyManager);
    }

    public static TaskManager getDefault(Repository<Task> taskRepository,
                                         Repository<Subtask> subtaskRepository,
                                         Repository<Epic> epicRepository,
                                         HistoryRepository historyRepository) {

        HistoryManager historyManager = new HistoryManagerImpl(historyRepository);
        return new TaskManagerImpl(taskRepository, epicRepository, subtaskRepository, historyManager);
    }

    public static TaskManager fileBackedTaskManager(Path pathToFile) {

        FileDataSource dataSource = createDataSource(pathToFile);

        Repository<Task> taskRepository = new InFileTaskRepositoryImpl(dataSource);
        Repository<Epic> epicRepository = new InFileEpicRepositoryImpl(dataSource);
        Repository<Subtask> subtaskRepository = new InFileSubtaskRepositoryImpl(dataSource);

        HistoryRepository historyRepository = createDefaultHistoryRepository();
        HistoryManager historyManager = new HistoryManagerImpl(historyRepository);

        return new TaskManagerImpl(taskRepository, epicRepository, subtaskRepository, historyManager);
    }

    private static HistoryRepository createDefaultHistoryRepository() {
        return new InMemoryHistoryRepositoryWithCopyAndLimitElements();
    }

    private static FileDataSource createDataSource(Path pathToFile) {
        return new CSVFileDataSource(pathToFile);
    }
}