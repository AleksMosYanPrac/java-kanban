package ru.yandex.practicum.kanban.service.services.impls;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.HistoryRepository;
import ru.yandex.practicum.kanban.service.services.HistoryService;

import java.util.List;

public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;

    public HistoryServiceImpl(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Override
    public Task add(Task task) {
        historyRepository.add(task);
        return task;
    }

    @Override
    public void remove(int id) {
        historyRepository.delete(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyRepository.list();
    }
}