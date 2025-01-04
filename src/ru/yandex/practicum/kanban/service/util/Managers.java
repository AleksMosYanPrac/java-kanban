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

    public static TaskManager fileStoredTaskManager(Path pathToFile) {

        FileDataSource dataSource = createDataSource(pathToFile);

        Repository<Task> taskRepository = new InFileTaskRepositoryImpl(dataSource);
        Repository<Epic> epicRepository = new InFileEpicRepositoryImpl(dataSource);
        Repository<Subtask> subtaskRepository = new InFileSubtaskRepositoryImpl(dataSource);

        HistoryRepository historyRepository = createDefaultHistoryRepository();
        HistoryManager historyManager = new HistoryManagerImpl(historyRepository);

        return new TaskManagerImpl(taskRepository, epicRepository, subtaskRepository, historyManager);
    }

    public static TaskManager fileBackedTaskManager(Path pathToFile){
        BackedRepository<Task> taskRepository = new InMemoryTaskRepositoryWithBackupData();
        BackedRepository<Epic> epicRepository = new InMemoryEpicRepositoryWithBackupData();
        BackedRepository<Subtask> subtaskRepository = new InMemorySubtaskRepositoryWithBackupData();

        HistoryRepository historyRepository = createDefaultHistoryRepository();
        HistoryManager historyManager = new HistoryManagerImpl(historyRepository);

        return new FileBackedTaskManager(taskRepository,epicRepository,subtaskRepository,historyManager,pathToFile);
    }

    private static HistoryRepository createDefaultHistoryRepository() {
        return new InMemoryHistoryRepositoryWithCopyAndLimitElements();
    }

    private static FileDataSource createDataSource(Path pathToFile) {
        return new CSVFileDataSource(pathToFile);
    }
}