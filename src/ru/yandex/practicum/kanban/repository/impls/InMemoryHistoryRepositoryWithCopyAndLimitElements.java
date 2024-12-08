package ru.yandex.practicum.kanban.repository.impls;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.HistoryRepository;

import java.util.*;

public class InMemoryHistoryRepositoryWithCopyAndLimitElements implements HistoryRepository {
    private static int NUMBER_OF_ELEMENTS = 10;

    private Deque<Task> viewedTaskList;

    public InMemoryHistoryRepositoryWithCopyAndLimitElements() {
        this.viewedTaskList = new ArrayDeque<>();
    }

    @Override
    public void addToList(Task task) {
        viewedTaskList.offer(task);
        if (viewedTaskList.size() > NUMBER_OF_ELEMENTS) {
            viewedTaskList.pollFirst();
        }
    }

    @Override
    public List<Task> listOfViewedTasks() {
        return new ArrayList<>(viewedTaskList);
    }

    @Override
    public void deleteFromList(int id) {
        viewedTaskList.removeIf(task -> task.getId() == id);
    }
}