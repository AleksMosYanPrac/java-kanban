package ru.yandex.practicum.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.repository.HistoryRepository;
import ru.yandex.practicum.kanban.repository.Repository;
import ru.yandex.practicum.kanban.repository.impls.in_memory.InMemoryEpicRepositoryImpl;
import ru.yandex.practicum.kanban.repository.impls.in_memory.InMemoryHistoryRepositoryWithCopyAndLimitElements;
import ru.yandex.practicum.kanban.repository.impls.in_memory.InMemorySubtaskRepositoryImpl;
import ru.yandex.practicum.kanban.repository.impls.in_memory.InMemoryTaskRepositoryImpl;
import ru.yandex.practicum.kanban.service.managers.HistoryManager;
import ru.yandex.practicum.kanban.service.managers.HistoryManagerTest;
import ru.yandex.practicum.kanban.service.managers.TaskManagerTest;
import ru.yandex.practicum.kanban.service.services.HistoryService;
import ru.yandex.practicum.kanban.service.services.RepositoryService;
import ru.yandex.practicum.kanban.service.services.TaskService;
import ru.yandex.practicum.kanban.service.services.impls.HistoryServiceImpl;
import ru.yandex.practicum.kanban.service.services.impls.RepositoryServiceImpl;
import ru.yandex.practicum.kanban.service.services.impls.TaskServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.service.TestDataDTO.*;

class HistoricalTaskManagerWithCopyAndLimitInHistoryTest
        extends TaskManagerTest<HistoricalTaskManagerImpl>
        implements HistoryManagerTest {

    protected HistoryManager historyManager;

    protected TaskService taskService;
    protected RepositoryService repositoryService;
    protected HistoryService historyService;

    private Repository<Task> taskRepository = new InMemoryTaskRepositoryImpl();
    private Repository<Epic> epicRepository = new InMemoryEpicRepositoryImpl();
    private Repository<Subtask> subtaskRepository = new InMemorySubtaskRepositoryImpl();
    private HistoryRepository historyRepository = new InMemoryHistoryRepositoryWithCopyAndLimitElements();

    @BeforeEach
    protected void setUp() {
        this.taskService = new TaskServiceImpl();
        this.repositoryService = new RepositoryServiceImpl(taskRepository, epicRepository, subtaskRepository);
        this.historyService = new HistoryServiceImpl(historyRepository);

        historyManager = new HistoricalTaskManagerImpl(taskService, repositoryService, historyService);
        super.taskService = taskService;
        super.repositoryService = repositoryService;
        super.taskManager = (HistoricalTaskManagerImpl) historyManager;

    }

    @Test
    void canSafePreviouslyVersionOfTaskInHistoryManager() {
        Task task = historyManager.createTask(TASK_1);
        historyManager.getTaskById(task.getId());
        historyManager.updateTask(getWithIDandUpdatedStatus(task.getId(), Status.IN_PROGRESS));
        historyManager.getTaskById(task.getId());

        List<Task> viewedTasks = historyManager.getHistoryOfViewedTasks();

        assertEquals(2, viewedTasks.size());
        assertEquals(task.getTitle(), viewedTasks.getFirst().getTitle());
    }

    @Test
    @Override
    public void shouldAddTasksToViewedWhenGetTaskByIntegerId() {
        Epic epic = historyManager.createEpic(EPIC_1);
        Task task = historyManager.createTask(TASK_1);
        Subtask subtask = historyManager.createSubtask(SUBTASK_1);

        historyManager.getEpicById(epic.getId());
        historyManager.getTaskById(task.getId());
        historyManager.getSubTaskById(subtask.getId());

        assertAll("Check history list",
                () -> assertEquals(3, historyManager.getHistoryOfViewedTasks().size()),
                () -> assertTrue(historyManager.getHistoryOfViewedTasks().contains(epic)),
                () -> assertTrue(historyManager.getHistoryOfViewedTasks().contains(task)),
                () -> assertTrue(historyManager.getHistoryOfViewedTasks().contains(subtask))
        );
    }

    @Test
    public void shouldDeleteViewFromHistoryWhenUserDeleteTask() {
        Task task = taskManager.createTask(TASK_1);
        taskManager.getTaskById(task.getId());

        List<Task> history = historyManager.getHistoryOfViewedTasks();
        taskManager.deleteTask(getWithID(task.getId()));

        assertEquals(1, history.size());
        assertTrue(historyManager.getHistoryOfViewedTasks().isEmpty());
    }

    @Test
    void shouldDeleteViewFromHistoryWhenUserDeleteSubtask() {
        Subtask subtask = taskManager.createSubtask(SUBTASK_1);
        taskManager.getSubTaskById(subtask.getId());

        List<Task> history = historyManager.getHistoryOfViewedTasks();
        taskManager.deleteSubtask(getWithID(subtask.getId()));

        assertEquals(1, history.size());
        assertTrue(historyManager.getHistoryOfViewedTasks().isEmpty());
    }

    @Test
    @Override
    public void shouldDeleteViewFromHistoryWhenUserDeleteEpic() {
        Epic epic = taskManager.createEpic(EPIC_1);
        taskManager.getEpicById(epic.getId());

        List<Task> history = historyManager.getHistoryOfViewedTasks();
        taskManager.deleteEpic(getWithID(epic.getId()));

        assertEquals(1, history.size());
        assertTrue(historyManager.getHistoryOfViewedTasks().isEmpty());
    }

    @Test
    @Override
    public void shouldDeleteViewFromHistoryWhenUserDeleteEpicWithSubtasks() {
        Epic epic = historyManager.createEpic(EPIC_1, SUBTASK_1, SUBTASK_2);
        historyManager.getEpicById(epic.getId());

        List<Task> history = historyManager.getHistoryOfViewedTasks();
        historyManager.deleteEpic(getWithID(epic.getId()));

        assertEquals(1, history.size());
        assertTrue(historyManager.getHistoryOfViewedTasks().isEmpty());
    }
}