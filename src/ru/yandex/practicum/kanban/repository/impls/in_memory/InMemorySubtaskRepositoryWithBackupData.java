package ru.yandex.practicum.kanban.repository.impls.in_memory;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.TaskBuilder;
import ru.yandex.practicum.kanban.model.TaskBuilder.CSVParser;
import ru.yandex.practicum.kanban.repository.BackedRepository;
import ru.yandex.practicum.kanban.repository.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static ru.yandex.practicum.kanban.model.TaskBuilder.CSVParser.*;


public class InMemorySubtaskRepositoryWithBackupData implements BackedRepository<Subtask> {

    private final InnerSubtaskRepository subtaskRepository;

    public InMemorySubtaskRepositoryWithBackupData() {
        this.subtaskRepository = new InnerSubtaskRepository();
    }

    @Override
    public Repository<Subtask> getRepository() {
        return subtaskRepository;
    }

    @Override
    public void readData(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        subtaskRepository.subTasks = parseSubtasksFromCSVLines(lines);
    }

    @Override
    public void saveData(Path path) throws IOException {
        List<String> newLines = new ArrayList<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<Subtask> addedSubtasks = new ArrayList<>();
        for (String line : lines) {
            if (isSubtask(line)) {
                if (subtaskRepository.subTasks.containsKey(getIdFromCSVLine(line))) {
                    Subtask subtask = subtaskRepository.subTasks.get(getIdFromCSVLine(line));
                    newLines.add(subtaskToCSVLine(subtask));
                    addedSubtasks.add(subtask);
                }
            } else {
                newLines.add(line);
            }
        }
        List<Subtask> newSubtasks = subtaskRepository.subTasks.values()
                .stream().filter(subtask -> !addedSubtasks.contains(subtask)).toList();
        if (!newSubtasks.isEmpty()) {
            for (Subtask subtask : newSubtasks) {
                newLines.add(subtaskToCSVLine(subtask));
            }
        }
        Files.write(path, newLines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private class InnerSubtaskRepository implements Repository<Subtask> {

        private Map<Integer, Subtask> subTasks;

        public InnerSubtaskRepository() {
            this.subTasks = new HashMap<>();
        }

        public List<Subtask> getAll() {
            return new ArrayList<>(subTasks.values());
        }

        public void deleteAll() {
            subTasks.clear();
        }

        public Optional<Subtask> getById(int id) {
            return Optional.ofNullable(subTasks.get(id));
        }

        public void deleteById(int id) {
            subTasks.remove(id);
        }

        public void create(Subtask subTask) {
            subTasks.put(subTask.getId(), subTask);
        }

        public void update(Subtask subTask) {
            subTasks.replace(subTask.getId(), subTask);
        }
    }
}