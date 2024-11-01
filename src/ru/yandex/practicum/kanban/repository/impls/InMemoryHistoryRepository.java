package ru.yandex.practicum.kanban.repository.impls;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.HistoryRepository;

import java.util.*;

public class InMemoryHistoryRepository implements HistoryRepository {
    private static int NUMBER_OF_ELEMENTS = 10;

    private Deque<Task> viewedTask;

    public InMemoryHistoryRepository() {
        this.viewedTask = new ArrayDeque<>();
    }

    @Override
    public void add(Task task) {
        viewedTask.offer(task);
        if(viewedTask.size()>NUMBER_OF_ELEMENTS){
            viewedTask.pollFirst();
        }
    }

    @Override
    public List<Task> get() {
        return new ArrayList<>(viewedTask);
    }
}