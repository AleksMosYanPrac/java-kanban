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
        subtaskRepository.subTasks = subtasksFrom(lines);
    }

    private Map<Integer, Subtask> subtasksFrom(List<String> lines) {
        Map<Integer, Subtask> data = new HashMap<>();
        for (String line : lines) {
            String[] values = line.split(",", 7);
            if (values[1].equals("SUBTASK")) {
                Subtask subtask =
                        new Subtask(
                                Integer.parseInt(values[0]),
                                values[2],
                                values[4],
                                Status.valueOf(values[3])
                        );
                if (!values[6].isBlank()) {
                    Epic epic = findEpic(lines, values[6]);
                    subtask.addEpic(epic);
                }
                data.put(subtask.getId(), subtask);
            }
        }
        return data;
    }

    private Epic findEpic(List<String> lines, String id) {
        Epic epic = null;
        String subtasksId = "";
        for (String line : lines) {
            String[] values = line.split(",", 7);
            if (values[0].equals(id) && values[1].equals("EPIC")) {
                epic = new Epic(Integer.parseInt(values[0]), values[2], values[4], Status.valueOf(values[3]));
                subtasksId = values[7];
            }
        }
        if (Objects.nonNull(epic)) {
            List<Subtask> subtasksForEpic = findSubtasks(lines, subtasksId);
            for (Subtask subtask : subtasksForEpic) {
                epic.addSubtask(subtask);
                subtask.addEpic(epic);
            }
        }
        return epic;
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
        List<Subtask> addedSubtasks = new ArrayList<>();
        for (String line : lines) {
            String[] values = line.split(",", 7);
            if (values[1].equals("SUBTASK")) {
                if (subtaskRepository.subTasks.containsKey(Integer.parseInt(values[0]))) {
                    Subtask subtask = subtaskRepository.subTasks.get(Integer.parseInt(values[0]));
                    newLines.add(toString(subtask));
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
                newLines.add(toString(subtask));
            }
        }
        Files.write(path, newLines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private String toString(Subtask subtask) {
        String epicId = "";
        if (Objects.nonNull(subtask.getEpic())) {
            epicId = Integer.toString(subtask.getEpic().getId());
        }
        return String.format("%s,SUBTASK,%s,%s,%s,%s,",
                subtask.getId(),
                subtask.getTitle(),
                subtask.getStatus().toString(),
                subtask.getDescription(),
                epicId);
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