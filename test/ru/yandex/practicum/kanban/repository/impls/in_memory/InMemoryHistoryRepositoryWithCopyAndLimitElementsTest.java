package ru.yandex.practicum.kanban.repository.impls.in_memory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.HistoryRepository;

import java.util.ArrayList;
import java.util.List;

class InMemoryHistoryRepositoryWithCopyAndLimitElementsTest {

    static List<Task> tasks = new ArrayList<>();

    private HistoryRepository historyRepository;

    @BeforeAll
    static void init() {
        tasks.add(new Task(1, "task", "description", Status.NEW));
        tasks.add(new Subtask(2, "subtask", "description", Status.NEW));
        tasks.add(new Epic(3, "epic", "description", Status.NEW));
        tasks.add(new Task(4, "task", "description", Status.NEW));
        tasks.add(new Subtask(5, "subtask", "description", Status.NEW));
    }

    @BeforeEach
    void initEmptyRepository() {
        this.historyRepository = new InMemoryHistoryRepositoryWithCopyAndLimitElements();
    }

    @Test
    void canAddModelObjects() {
        for (Task task : tasks) {
            historyRepository.add(task);
        }
        Assertions.assertEquals(tasks, historyRepository.list());
    }

    @Test
    void canDeleteOldTaskAndPutNewWhenCapacityMoreThan10() {
        for (int i = 0; i < 11; i++) {
            Task task = new Task(i, "task", "description", Status.NEW);
            historyRepository.add(task);
        }

        List<Task> historyList = historyRepository.list();
        int taskIdAtFirstPosition = historyList.getFirst().getId();

        Assertions.assertEquals(1, taskIdAtFirstPosition);
        Assertions.assertEquals(10, historyList.size());
    }

    @Test
    void shouldDeleteTaskFromHistoryByIntegerId() {
        int historySize = historyRepository.list().size();
        int taskId = 100;
        Task task = new Task(taskId, "task", "description", Status.NEW);
        historyRepository.add(task);

        historyRepository.delete(taskId);

        Assertions.assertEquals(historySize, historyRepository.list().size());
        Assertions.assertFalse(historyRepository.list().contains(task));
    }
}