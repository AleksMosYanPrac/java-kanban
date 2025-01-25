package ru.yandex.practicum.kanban.repository.impls.in_memory;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.repository.BackedRepository;
import ru.yandex.practicum.kanban.repository.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static ru.yandex.practicum.kanban.model.TaskBuilder.CSVParser.*;

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
        epicRepository.epics = parseEpicsFromCSVLines(lines);
    }

    @Override
    public void saveData(Path path) throws IOException {
        List<String> newLines = new ArrayList<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<Epic> addedEpic = new ArrayList<>();
        for (String line : lines) {
            if (isEpic(line)) {
                int id = getIdFromCSVLine(line);
                if (epicRepository.epics.containsKey(id)) {
                    Epic epic = epicRepository.epics.get(id);
                    newLines.add(epicToCSVLine(epic));
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
                newLines.add(epicToCSVLine(epic));
            }
        }
        Files.write(path, newLines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
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