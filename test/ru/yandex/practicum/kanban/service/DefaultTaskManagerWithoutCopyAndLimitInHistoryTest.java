package ru.yandex.practicum.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.HistoryRepository;
import ru.yandex.practicum.kanban.repository.impls.in_memory.*;
import ru.yandex.practicum.kanban.service.managers.TaskManager;
import ru.yandex.practicum.kanban.service.services.impls.HistoryServiceImpl;
import ru.yandex.practicum.kanban.service.services.impls.PriorityServiceImpl;
import ru.yandex.practicum.kanban.service.services.impls.RepositoryServiceImpl;
import ru.yandex.practicum.kanban.service.services.impls.TaskServiceImpl;
import ru.yandex.practicum.kanban.service.util.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.service.TestDataDTO.*;

class DefaultTaskManagerWithoutCopyAndLimitInHistoryTest extends DefaultTaskManagerImplTest {

    protected TaskManager historyManager;

    private HistoryRepository historyRepository = new InMemoryHistoryRepositoryWithoutCopyAndLimitElements();

    @BeforeEach
    protected void setUp() {
        taskService = new TaskServiceImpl();
        repositoryService = new RepositoryServiceImpl(taskRepository, epicRepository, subtaskRepository);
        historyService = new HistoryServiceImpl(historyRepository);
        priorityService = new PriorityServiceImpl();
        historyManager = Managers.getDefault(taskRepository, subtaskRepository, epicRepository, historyRepository);
        super.taskManager = historyManager;
    }

    @Test
    void shouldNotSafePreviouslyVersionOfTaskInHistoryManager() throws Exception {
        Task task = historyManager.createTask(TASK_1);
        historyManager.getTaskById(task.getId());
        historyManager.updateTask(getWithIDandUpdatedStatus(task.getId(), Status.IN_PROGRESS));
        historyManager.getTaskById(task.getId());

        List<Task> viewedTasks = historyManager.getHistoryOfViewedTasks();

        assertNotEquals(2, viewedTasks.size());
        assertNotEquals(task.getTitle(), viewedTasks.getFirst().getTitle());
    }

    @Test
    public void shouldAddTasksToViewedWhenGetTaskByIntegerId() throws Exception {
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
    public void shouldDeleteViewFromHistoryWhenUserDeleteTask() throws Exception {
        Task task = taskManager.createTask(TASK_1);
        taskManager.getTaskById(task.getId());

        List<Task> history = taskManager.getHistoryOfViewedTasks();
        taskManager.deleteTask(task.getId());

        assertEquals(1, history.size());
        assertTrue(taskManager.getHistoryOfViewedTasks().isEmpty());
    }

    @Test
    void shouldDeleteViewFromHistoryWhenUserDeleteSubtask() throws Exception {
        Subtask subtask = taskManager.createSubtask(SUBTASK_1);
        taskManager.getSubTaskById(subtask.getId());

        List<Task> history = taskManager.getHistoryOfViewedTasks();
        taskManager.deleteSubtask(subtask.getId());

        assertEquals(1, history.size());
        assertTrue(taskManager.getHistoryOfViewedTasks().isEmpty());
    }

    @Test
    public void shouldDeleteViewFromHistoryWhenUserDeleteEpic() throws Exception {
        Epic epic = taskManager.createEpic(EPIC_1);
        taskManager.getEpicById(epic.getId());

        List<Task> history = taskManager.getHistoryOfViewedTasks();
        taskManager.deleteEpic(epic.getId());

        assertEquals(1, history.size());
        assertTrue(taskManager.getHistoryOfViewedTasks().isEmpty());
    }

    @Test
    public void shouldDeleteViewFromHistoryWhenUserDeleteEpicWithSubtasks() throws Exception {
        Epic epic = taskManager.createEpic(EPIC_1, SUBTASK_1, SUBTASK_2);
        historyManager.getEpicById(epic.getId());

        List<Task> history = historyManager.getHistoryOfViewedTasks();
        historyManager.deleteEpic(epic.getId());

        assertEquals(1, history.size());
        assertTrue(taskManager.getHistoryOfViewedTasks().isEmpty());
    }
}