package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.HistoryRepository;

import java.util.List;

public class HistoryManagerImpl implements HistoryManager{

    private final HistoryRepository historyRepository;

    public HistoryManagerImpl(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Override
    public void add(Task task) {
        historyRepository.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyRepository.get();
    }
}