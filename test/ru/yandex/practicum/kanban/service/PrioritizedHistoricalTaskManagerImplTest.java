package ru.yandex.practicum.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskDTO;
import ru.yandex.practicum.kanban.service.exceptions.PriorityManagerTimeIntersection;
import ru.yandex.practicum.kanban.service.managers.HistoryManager;
import ru.yandex.practicum.kanban.service.managers.PriorityManager;
import ru.yandex.practicum.kanban.service.managers.PriorityManagerTest;
import ru.yandex.practicum.kanban.service.services.PriorityService;
import ru.yandex.practicum.kanban.service.services.impls.PriorityServiceImpl;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.service.TestDataDTO.*;

class PrioritizedHistoricalTaskManagerImplTest
        extends HistoricalTaskManagerWithoutCopyAndLimitInHistoryTest
        implements PriorityManagerTest {

    private PriorityManager priorityManager;
    PriorityService priorityService;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        priorityService = new PriorityServiceImpl();
        priorityManager = new PrioritizedHistoricalTaskManagerImpl(
                taskService,
                repositoryService,
                historyService,
                priorityService);
        historyManager = (HistoryManager) priorityManager;
    }

    @Test
    public void shouldThrowStartTimeExceptionWhenAddTaskWithTimeIntersection() {
        priorityManager.createTask(getDatedTask());
        priorityManager.createSubtask(getDatedSubtask());

        assertThrows(PriorityManagerTimeIntersection.class,
                () -> priorityManager.createTask(getDatedTask()));
        assertThrows(PriorityManagerTimeIntersection.class,
                () -> priorityManager.createSubtask(getDatedSubtask()));
        assertThrows(PriorityManagerTimeIntersection.class,
                () -> priorityManager.createEpic(EPIC_1, getDatedSubtask()));
    }

    @Test
    public void shouldThrowStartTimeExceptionWhenUpdatedTaskHasTimeIntersection() {
        String date1 = "00:00 01.01.2025";
        String date2 = "00:00 02.01.2025";
        String intersection = "00:30 02.01.2025";
        Task task_1 = priorityManager.createTask(
                new TaskDTO("task1","-","NEW",date1,60));
        Task task_2 = priorityManager.createTask(
                new TaskDTO("task2","-","NEW",date2,60));
        TaskDTO updatedTask_1 =
                new TaskDTO(task_1.getId(),"task1","-","NEW",intersection,60);

        assertThrows(PriorityManagerTimeIntersection.class,
                () -> priorityManager.updateTask(updatedTask_1));
    }

    @Test
    public void shouldReturnSortedByStartTimeTaskSet() {
        priorityManager.createTask(getDatedTask());
        priorityManager.createSubtask(getDatedSubtask());

        TreeSet<Task> prioritizedTasks = priorityManager.getPrioritizedTasks();

        assertTrue(prioritizedTasks.first().getStartTime().isBefore(prioritizedTasks.last().getStartTime()));
    }
}