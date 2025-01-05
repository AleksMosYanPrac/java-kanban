package ru.yandex.practicum.kanban.repository.impls.in_memory;

import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.BackedRepository;
import ru.yandex.practicum.kanban.repository.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class InMemoryTaskRepositoryWithBackupData implements BackedRepository<Task> {

    private final InnerTaskRepository taskRepository;

    public InMemoryTaskRepositoryWithBackupData() {
        this.taskRepository = new InnerTaskRepository();
    }

    @Override
    public Repository<Task> getRepository() {
        return taskRepository;
    }

    @Override
    public void readData(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        taskRepository.tasks = tasksFrom(lines);
    }

    private Map<Integer, Task> tasksFrom(List<String> lines) {
        Map<Integer, Task> data = new HashMap<>();
        for (String line : lines) {
            String[] values = line.split(",", 7);
            if (values[1].equals("TASK")) {
                Task task =
                        new Task(
                                Integer.parseInt(values[0]),
                                values[2],
                                values[4],
                                Status.valueOf(values[3])
                        );
                data.put(task.getId(), task);
            }
        }
        return data;
    }

    @Override
    public void saveData(Path path) throws IOException {
        List<String> newLines = new ArrayList<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<Task> addedTasks = new ArrayList<>();
        for (String line : lines) {
            String[] values = line.split(",", 7);
            if (values[1].equals("TASK")) {
                if (taskRepository.tasks.containsKey(Integer.parseInt(values[0]))) {
                    Task task = taskRepository.tasks.get(Integer.parseInt(values[0]));
                    newLines.add(toString(task));
                    addedTasks.add(task);
                }
            } else {
                newLines.add(line);
            }
        }
        List<Task> newTasks = taskRepository.tasks.values()
                .stream()
                .filter(task -> !addedTasks.contains(task))
                .toList();
        if (!newTasks.isEmpty()) {
            for (Task task : newTasks) {
                newLines.add(toString(task));
            }
        }
        Files.write(path, newLines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private String toString(Task task) {
        return String.format("%s,TASK,%s,%s,%s,,",
                task.getId(),
                task.getTitle(),
                task.getStatus().toString(),
                task.getDescription());
    }

    private class InnerTaskRepository implements Repository<Task> {

        private Map<Integer, Task> tasks;

        public InnerTaskRepository() {
            this.tasks = new HashMap<>();
        }

        @Override
        public List<Task> getAll() {
            return new ArrayList<>(tasks.values());
        }

        @Override
        public void deleteAll() {
            tasks.clear();
        }

        @Override
        public Optional<Task> getById(int id) {
            return Optional.ofNullable(tasks.get(id));
        }

        @Override
        public void deleteById(int id) {
            tasks.remove(id);
        }

        @Override
        public void create(Task task) {
            tasks.put(task.getId(), task);
        }

        @Override
        public void update(Task task) {
            tasks.replace(task.getId(), task);
        }
    }
}