package ru.yandex.practicum.kanban.repository.impls.in_memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.HistoryRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.repository.TestData.*;

class InMemoryHistoryRepositoryWithoutCopyAndLimitElementsTest {

    static List<Task> tasks = getListTasks();

    private HistoryRepository historyRepository;

    @BeforeEach
    void initEmptyRepository() {
        this.historyRepository = new InMemoryHistoryRepositoryWithoutCopyAndLimitElements();
    }

    @Test
    void canAddModelObjects() {
        for (Task task : tasks) {
            historyRepository.add(task);
        }
        assertEquals(tasks, historyRepository.list());
    }

    @Test
    void shouldDeleteTaskFromHistoryByIntegerId() {
        int historySize = historyRepository.list().size();
        int taskId = 100;
        Task task = new Task(taskId, "task", "description", Status.NEW);
        historyRepository.add(task);

        historyRepository.delete(taskId);

        assertEquals(historySize, historyRepository.list().size());
        assertFalse(historyRepository.list().contains(task));
    }

    @Test
    void canAddMoreThan10Elements() {
        List<Task> listOfAddedTask = new ArrayList<>();

        for (int i = 1; i < 100; i++) {
            Task task = new Task(i, "task", "description", Status.NEW);
            listOfAddedTask.add(task);
            historyRepository.add(task);
        }
        assertEquals(listOfAddedTask, historyRepository.list());
    }

    @Test
    void shouldNotSaveRepeatedView() {
        List<Task> listBeforeAdd = historyRepository.list();
        int expectedSizeAfterAdd = 1;

        historyRepository.add(TASK_1);
        historyRepository.add(TASK_1);

        assertTrue(listBeforeAdd.isEmpty());
        assertEquals(expectedSizeAfterAdd, historyRepository.list().size());
    }

    @Test
    void shouldSaveInRightOrderWhenLastAddedToLinkedListIsHead() {
        List<Task> rightOrder = List.of(TASK_2, SUBTASK_1, SUBTASK_2, EPIC_1, TASK_1);

        historyRepository.add(TASK_1);
        historyRepository.add(TASK_2);
        historyRepository.add(SUBTASK_1);
        historyRepository.add(SUBTASK_2);
        historyRepository.add(EPIC_1);
        historyRepository.add(TASK_1);

        assertEquals(rightOrder.size(), historyRepository.list().size());
        assertEquals(rightOrder, historyRepository.list());
    }

    @Test
    void shouldSaveInRightOrderWhenLastAddedToLinkedListIsTail() {
        List<Task> rightOrder = List.of(TASK_1, TASK_2, SUBTASK_1, SUBTASK_2, EPIC_1);

        historyRepository.add(TASK_1);
        historyRepository.add(TASK_2);
        historyRepository.add(SUBTASK_1);
        historyRepository.add(SUBTASK_2);
        historyRepository.add(EPIC_1);
        historyRepository.add(EPIC_1);

        assertEquals(rightOrder.size(), historyRepository.list().size());
        assertEquals(rightOrder, historyRepository.list());
    }

    @Test
    void shouldSaveInRightOrderWhenLastAddedToLinkedListIsBetweenHeadAndTail() {
        List<Task> rightOrder = List.of(TASK_1, TASK_2, SUBTASK_2, EPIC_1, SUBTASK_1);

        historyRepository.add(TASK_1);
        historyRepository.add(TASK_2);
        historyRepository.add(SUBTASK_1);
        historyRepository.add(SUBTASK_2);
        historyRepository.add(EPIC_1);
        historyRepository.add(SUBTASK_1);

        assertEquals(rightOrder.size(), historyRepository.list().size());
        assertEquals(rightOrder, historyRepository.list());
    }
}