package ru.yandex.practicum.kanban.repository.impls.in_memory;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.repository.BackedRepository;
import ru.yandex.practicum.kanban.repository.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class InMemoryEpicRepositoryWithBackupData implements BackedRepository<Epic> {

    private final InnerEpicRepository epicRepository;

    public InMemoryEpicRepositoryWithBackupData() {
        this.epicRepository = new InnerEpicRepository();
    }

    @Override
    public Repository<Epic> getRepository() {
        return epicRepository;
    }

    @Override
    public void readData(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        epicRepository.epics = epicsFrom(lines);
    }

    private Map<Integer, Epic> epicsFrom(List<String> lines) {
        Map<Integer, Epic> data = new HashMap<>();
        for (String line : lines) {
            String[] values = line.split(",", 7);
            if (values[1].equals("EPIC")) {
                Epic epic = new Epic(Integer.parseInt(values[0]), values[2], values[4], Status.valueOf(values[3]));
                List<Subtask> subtasks = findSubtasks(lines, values[7]);
                if (!subtasks.isEmpty()) {
                    for (Subtask subtask : subtasks) {
                        epic.addSubtask(subtask);
                    }
                }
                data.put(epic.getId(), epic);
            }
        }
        return data;
    }

    private List<Subtask> findSubtasks(List<String> lines, String idOfSubtasks) {
        List<Subtask> subtasks = new ArrayList<>();
        if (idOfSubtasks.length() > 2) {
            String substring = idOfSubtasks.substring(1, idOfSubtasks.length() - 1);
            String[] idArray = substring.split(",");
            for (String id : idArray) {
                for (String line : lines) {
                    String[] values = line.split(",", 7);
                    if (values[0].equals(id) & values[1].equals("SUBTASK")) {
                        Subtask subtask =
                                new Subtask(
                                        Integer.parseInt(values[0]),
                                        values[2],
                                        values[4],
                                        Status.valueOf(values[3])
                                );
                        subtasks.add(subtask);
                    }
                }
            }
        }
        return subtasks;
    }

    @Override
    public void saveData(Path path) throws IOException {
        List<String> newLines = new ArrayList<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<Epic> addedEpic = new ArrayList<>();
        for (String line : lines) {
            String[] values = line.split(",", 7);
            if (values[1].equals("EPIC")) {
                if (epicRepository.epics.containsKey(Integer.parseInt(values[0]))) {
                    Epic epic = epicRepository.epics.get(Integer.parseInt(values[0]));
                    newLines.add(toString(epic));
                    addedEpic.add(epic);
                }
            } else {
                newLines.add(line);
            }
        }
        List<Epic> newEpics = epicRepository.epics.values()
                .stream()
                .filter(epic -> !addedEpic.contains(epic))
                .toList();
        if (!newEpics.isEmpty()) {
            for (Epic epic : newEpics) {
                newLines.add(toString(epic));
            }
        }
        Files.write(path, newLines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private String toString(Epic epic) {
        StringBuilder idOfSubtasks = new StringBuilder();
        List<Subtask> subtasks = epic.getSubtasks();
        idOfSubtasks.append("[");
        for (Subtask subtask : subtasks) {
            idOfSubtasks.append(subtask.getId());
            if (subtasks.indexOf(subtask) < subtasks.size() - 1) {
                idOfSubtasks.append(",");
            }
        }
        idOfSubtasks.append("]");
        return String.format("%s,EPIC,%s,%s,%s,,%s",
                epic.getId(),
                epic.getTitle(),
                epic.getStatus().toString(),
                epic.getDescription(),
                idOfSubtasks.toString());
    }

    private class InnerEpicRepository implements Repository<Epic> {

        private Map<Integer, Epic> epics;

        public InnerEpicRepository() {
            this.epics = new HashMap<>();
        }

        public List<Epic> getAll() {
            return new ArrayList<>(epics.values());
        }

        public void deleteAll() {
            epics.clear();
        }

        public Optional<Epic> getById(int id) {
            return Optional.ofNullable(epics.get(id));
        }

        public void deleteById(int id) {
            epics.remove(id);
        }

        public void create(Epic epic) {
            epics.put(epic.getId(), epic);
        }

        public void update(Epic epic) {
            epics.replace(epic.getId(), epic);
        }
    }
}