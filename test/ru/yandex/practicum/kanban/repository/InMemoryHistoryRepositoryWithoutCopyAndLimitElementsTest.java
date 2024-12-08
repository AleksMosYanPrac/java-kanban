package ru.yandex.practicum.kanban.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.impls.InMemoryHistoryRepositoryWithoutCopyAndLimitElements;

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
    void canAddToListModelObjects() {
        for (Task task : tasks) {
            historyRepository.addToList(task);
        }
        assertEquals(tasks, historyRepository.listOfViewedTasks());
    }

    @Test
    void shouldDeleteTaskFromHistoryByIntegerId() {
        int historySize = historyRepository.listOfViewedTasks().size();
        int taskId = 100;
        Task task = new Task(taskId, "task", "description", Status.NEW);
        historyRepository.addToList(task);

        historyRepository.deleteFromList(taskId);

        assertEquals(historySize, historyRepository.listOfViewedTasks().size());
        assertFalse(historyRepository.listOfViewedTasks().contains(task));
    }

    @Test
    void canAddToListMoreThan10Elements() {
        List<Task> listOfAddedTask = new ArrayList<>();

        for (int i = 1; i < 100; i++) {
            Task task = new Task(i, "task", "description", Status.NEW);
            listOfAddedTask.add(task);
            historyRepository.addToList(task);
        }
        assertEquals(listOfAddedTask, historyRepository.listOfViewedTasks());
    }

    @Test
    void shouldNotSaveRepeatedView() {
        List<Task> listBeforeAdd = historyRepository.listOfViewedTasks();
        int expectedSizeAfterAdd = 1;

        historyRepository.addToList(TASK_1);
        historyRepository.addToList(TASK_1);

        assertTrue(listBeforeAdd.isEmpty());
        assertEquals(expectedSizeAfterAdd, historyRepository.listOfViewedTasks().size());
    }

    @Test
    void shouldSaveInRightOrderWhenLastAddedToLinkedListIsHead() {
        List<Task> rightOrder = List.of(TASK_2, SUBTASK_1, SUBTASK_2, EPIC_1, TASK_1);

        historyRepository.addToList(TASK_1);
        historyRepository.addToList(TASK_2);
        historyRepository.addToList(SUBTASK_1);
        historyRepository.addToList(SUBTASK_2);
        historyRepository.addToList(EPIC_1);
        historyRepository.addToList(TASK_1);

        assertEquals(rightOrder.size(), historyRepository.listOfViewedTasks().size());
        assertEquals(rightOrder, historyRepository.listOfViewedTasks());
    }

    @Test
    void shouldSaveInRightOrderWhenLastAddedToLinkedListIsTail() {
        List<Task> rightOrder = List.of(TASK_1, TASK_2, SUBTASK_1, SUBTASK_2, EPIC_1);

        historyRepository.addToList(TASK_1);
        historyRepository.addToList(TASK_2);
        historyRepository.addToList(SUBTASK_1);
        historyRepository.addToList(SUBTASK_2);
        historyRepository.addToList(EPIC_1);
        historyRepository.addToList(EPIC_1);

        assertEquals(rightOrder.size(), historyRepository.listOfViewedTasks().size());
        assertEquals(rightOrder, historyRepository.listOfViewedTasks());
    }

    @Test
    void shouldSaveInRightOrderWhenLastAddedToLinkedListIsBetweenHeadAndTail() {
        List<Task> rightOrder = List.of(TASK_1, TASK_2, SUBTASK_2, EPIC_1, SUBTASK_1);

        historyRepository.addToList(TASK_1);
        historyRepository.addToList(TASK_2);
        historyRepository.addToList(SUBTASK_1);
        historyRepository.addToList(SUBTASK_2);
        historyRepository.addToList(EPIC_1);
        historyRepository.addToList(SUBTASK_1);

        assertEquals(rightOrder.size(), historyRepository.listOfViewedTasks().size());
        assertEquals(rightOrder, historyRepository.listOfViewedTasks());
    }
}