package ru.yandex.practicum.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.repository.HistoryRepository;
import ru.yandex.practicum.kanban.repository.impls.in_memory.InMemoryHistoryRepositoryWithCopyAndLimitElements;
import ru.yandex.practicum.kanban.service.managers.TaskManager;
import ru.yandex.practicum.kanban.service.services.impls.HistoryServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.service.TestDataDTO.*;

class DefaultTaskManagerWithCopyAndLimitInHistoryTest extends DefaultTaskManagerImplTest {

    protected TaskManager taskManager;

    private HistoryRepository historyRepository = new InMemoryHistoryRepositoryWithCopyAndLimitElements();

    @BeforeEach
    protected void setUp() {
        super.setUp();
        super.historyService = new HistoryServiceImpl(historyRepository);
        this.taskManager = super.taskManager;
    }

    @Test
    void canSafePreviouslyVersionOfTaskInHistoryManager() throws Exception {
        Task task = taskManager.createTask(TASK_1);
        taskManager.getTaskById(task.getId());
        taskManager.updateTask(getWithIDandUpdatedStatus(task.getId(), Status.IN_PROGRESS));
        taskManager.getTaskById(task.getId());

        List<Task> viewedTasks = taskManager.getHistoryOfViewedTasks();

        assertEquals(2, viewedTasks.size());
        assertEquals(task.getTitle(), viewedTasks.getFirst().getTitle());
    }

    @Test
    public void shouldAddTasksToViewedWhenGetTaskByIntegerId() throws Exception {
        Epic epic = taskManager.createEpic(EPIC_1);
        Task task = taskManager.createTask(TASK_1);
        Subtask subtask = taskManager.createSubtask(SUBTASK_1);

        taskManager.getEpicById(epic.getId());
        taskManager.getTaskById(task.getId());
        taskManager.getSubTaskById(subtask.getId());

        assertAll("Check history list",
                () -> assertEquals(3, taskManager.getHistoryOfViewedTasks().size()),
                () -> assertTrue(taskManager.getHistoryOfViewedTasks().contains(epic)),
                () -> assertTrue(taskManager.getHistoryOfViewedTasks().contains(task)),
                () -> assertTrue(taskManager.getHistoryOfViewedTasks().contains(subtask))
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
        taskManager.getEpicById(epic.getId());

        List<Task> history = taskManager.getHistoryOfViewedTasks();
        taskManager.deleteEpic(epic.getId());

        assertEquals(1, history.size());
        assertTrue(taskManager.getHistoryOfViewedTasks().isEmpty());
    }
}