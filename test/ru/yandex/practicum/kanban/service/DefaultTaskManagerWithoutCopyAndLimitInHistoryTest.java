package ru.yandex.practicum.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.HistoryRepository;
import ru.yandex.practicum.kanban.repository.Repository;
import ru.yandex.practicum.kanban.repository.impls.in_memory.InMemoryEpicRepositoryImpl;
import ru.yandex.practicum.kanban.repository.impls.in_memory.InMemoryHistoryRepositoryWithoutCopyAndLimitElements;
import ru.yandex.practicum.kanban.repository.impls.in_memory.InMemorySubtaskRepositoryImpl;
import ru.yandex.practicum.kanban.repository.impls.in_memory.InMemoryTaskRepositoryImpl;
import ru.yandex.practicum.kanban.service.util.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.service.TestDataDTO.*;

class DefaultTaskManagerWithoutCopyAndLimitInHistoryTest {

    private TaskManager taskManager;
    private HistoryRepository historyRepository = new InMemoryHistoryRepositoryWithoutCopyAndLimitElements();

    private Repository<Task> taskRepository = new InMemoryTaskRepositoryImpl();
    private Repository<Epic> epicRepository = new InMemoryEpicRepositoryImpl();
    private Repository<Subtask> subtaskRepository = new InMemorySubtaskRepositoryImpl();

    @BeforeEach
    void initTaskManager() {
        taskManager = Managers.getDefault(taskRepository, subtaskRepository, epicRepository, historyRepository);
    }

    @Test
    void canAddDifferentTypeTask() {

        taskManager.createTask(TASK_1);
        taskManager.createEpic(EPIC_1);
        taskManager.createSubtask(SUBTASK_1);

        assertEquals(1, taskRepository.getAll().size());
        assertEquals(1, epicRepository.getAll().size());
        assertEquals(1, subtaskRepository.getAll().size());
    }

    @Test
    void shouldNotSafePreviouslyVersionOfTaskInHistoryManager() {
        Task task = taskManager.createTask(TASK_1);
        taskManager.getTaskById(task.getId());
        taskManager.updateTask(getWithIDandUpdatedStatus(task.getId(), Status.IN_PROGRESS));
        taskManager.getTaskById(task.getId());

        List<Task> viewedTasks = taskManager.getHistoryOfViewedTasks();

        assertNotEquals(2, viewedTasks.size());
        assertNotEquals(task.getTitle(), viewedTasks.getFirst().getTitle());
    }

    @Test
    void shouldAddToHistory() {
        Epic epic = taskManager.createEpic(EPIC_1);

        taskManager.getEpicById(epic.getId());
        List<Task> viewedTasks = taskManager.getHistoryOfViewedTasks();

        assertEquals(1, viewedTasks.size());
        assertEquals(EPIC_1.getTitle(), viewedTasks.getFirst().getTitle());
    }

    @Test
    void shouldCreateEpicWithLinkedSubtask() {

        taskManager.createEpic(EPIC_1, SUBTASK_1);
        Epic addedEpic = epicRepository.getAll().getFirst();
        Subtask addedSubtask = subtaskRepository.getAll().getFirst();

        assertEquals(addedEpic, addedSubtask.getEpic());
        assertEquals(addedSubtask, addedEpic.getSubtasks().getFirst());
    }

    @Test
    void shouldDeleteSubtaskWhenDeleteEpic() {
        int id = taskManager.createEpic(EPIC_1, SUBTASK_1).getId();
        Epic addedEpic = epicRepository.getAll().getFirst();
        Subtask addedSubtask = subtaskRepository.getAll().getFirst();

        taskManager.deleteEpic(getWithID(addedEpic.getId()));

        assertTrue(epicRepository.getAll().isEmpty());
        assertTrue(subtaskRepository.getAll().isEmpty());
    }

    @Test
    void shouldUnlinkDeletedSubtaskAndUpdateEpic() {
        taskManager.createEpic(EPIC_1, SUBTASK_1);

        Subtask addedSubtask = subtaskRepository.getAll().getFirst();
        taskManager.deleteSubtask(getWithID(addedSubtask.getId()));
        Epic updatedEpic = epicRepository.getAll().getFirst();

        assertTrue(subtaskRepository.getAll().isEmpty());
        assertTrue(updatedEpic.getSubtasks().isEmpty());
    }

    @Test
    void shouldDeleteViewFromHistoryWhenUserDeleteTask() {
        Task task = taskManager.createTask(TASK_1);
        taskManager.getTaskById(task.getId());

        List<Task> history = taskManager.getHistoryOfViewedTasks();
        taskManager.deleteTask(getWithID(task.getId()));

        assertEquals(1, history.size());
        assertTrue(taskManager.getHistoryOfViewedTasks().isEmpty());
    }

    @Test
    void shouldDeleteViewFromHistoryWhenUserDeleteSubtask() {
        Subtask subtask = taskManager.createSubtask(SUBTASK_1);
        taskManager.getSubTaskById(subtask.getId());

        List<Task> history = taskManager.getHistoryOfViewedTasks();
        taskManager.deleteSubtask(getWithID(subtask.getId()));

        assertEquals(1, history.size());
        assertTrue(taskManager.getHistoryOfViewedTasks().isEmpty());
    }

    @Test
    void shouldDeleteViewFromHistoryWhenUserDeleteEpic() {
        Epic epic = taskManager.createEpic(EPIC_1);
        taskManager.getEpicById(epic.getId());

        List<Task> history = taskManager.getHistoryOfViewedTasks();
        taskManager.deleteEpic(getWithID(epic.getId()));

        assertEquals(1, history.size());
        assertTrue(taskManager.getHistoryOfViewedTasks().isEmpty());
    }

    @Test
    void shouldDeleteViewFromHistoryWhenUserDeleteEpicWithSubtasks() {
        Epic epic = taskManager.createEpic(EPIC_1, SUBTASK_1, SUBTASK_2);
        taskManager.getEpicById(epic.getId());

        List<Task> history = taskManager.getHistoryOfViewedTasks();
        taskManager.deleteEpic(getWithID(epic.getId()));

        assertEquals(1, history.size());
        assertTrue(taskManager.getHistoryOfViewedTasks().isEmpty());
    }
}