package ru.yandex.practicum.kanban.repository.impls;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.HistoryRepository;

import java.util.*;

public class InMemoryHistoryRepositoryWithCopyAndLimitElements implements HistoryRepository {
    private static int NUMBER_OF_ELEMENTS = 10;

    private Deque<Task> viewedTask;

    public InMemoryHistoryRepositoryWithCopyAndLimitElements() {
        this.viewedTask = new ArrayDeque<>();
    }

    @Override
    public void add(Task task) {
        viewedTask.offer(task);
        if (viewedTask.size() > NUMBER_OF_ELEMENTS) {
            viewedTask.pollFirst();
        }
    }

    @Override
    public List<Task> get() {
        return new ArrayList<>(viewedTask);
    }

    @Override
    public void deleteFromHistory(int id) {
        viewedTask.removeIf(task -> task.getId() == id);
    }
}