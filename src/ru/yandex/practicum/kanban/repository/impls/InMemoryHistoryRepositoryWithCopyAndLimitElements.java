package ru.yandex.practicum.kanban.repository.impls;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.HistoryRepository;

import java.util.*;

public class InMemoryHistoryRepositoryWithCopyAndLimitElements implements HistoryRepository {
    private static int NUMBER_OF_ELEMENTS = 10;

    private Deque<Task> viewedTasks;

    public InMemoryHistoryRepositoryWithCopyAndLimitElements() {
        this.viewedTasks = new ArrayDeque<>();
    }

    @Override
    public void add(Task task) {
        viewedTasks.offer(task);
        if (viewedTasks.size() > NUMBER_OF_ELEMENTS) {
            viewedTasks.pollFirst();
        }
    }

    @Override
    public List<Task> list() {
        return new ArrayList<>(viewedTasks);
    }

    @Override
    public void delete(int id) {
        viewedTasks.removeIf(task -> task.getId() == id);
    }
}