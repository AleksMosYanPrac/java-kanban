package ru.yandex.practicum.kanban.repository.impls.in_file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.repository.impls.in_file.datasource.CSVFileDataSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.repository.TestData.*;

class InFileSubtaskRepositoryWithCSVFileDataSourceTest {

    private Path pathToFile;
    private InFileSubtaskRepositoryImpl subtaskRepository;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws InvalidPathException, IOException {
        pathToFile = tempDir.resolve("temp_file_1.csv");
        Files.createFile(pathToFile);
        this.subtaskRepository = new InFileSubtaskRepositoryImpl(new CSVFileDataSource(pathToFile));
    }

    @Test
    void shouldAppendSubtaskWithoutEpicToFileAsNewLastLine() throws IOException {
        Subtask subtask = SUBTASK_1;
        java.lang.String subtaskAsCSVLine = subtaskToCSVLine(subtask);

        subtaskRepository.create(subtask);
        java.lang.String lastLineInFile = Files.readAllLines(pathToFile, StandardCharsets.UTF_8).getLast();

        assertEquals(subtaskAsCSVLine, lastLineInFile);
    }

    @Test
    void shouldAppendSubtaskWithEpicToFileAsNewLastLine() throws IOException {
        Subtask subtask = SUBTASK_1;
        Epic epic = EPIC_1;
        subtask.addEpic(epic);
        java.lang.String subtaskAsCSVLine = subtaskToCSVLine(subtask);

        subtaskRepository.create(subtask);
        java.lang.String lastLineInFile = Files.readAllLines(pathToFile, StandardCharsets.UTF_8).getLast();

        assertEquals(subtaskAsCSVLine, lastLineInFile);
    }

    @Test
    void shouldOverwriteStringLineInFileForUpdatedSubtask() throws IOException {
        Subtask subtask = new Subtask(1, "subtask", "description", Status.NEW);
        java.lang.String subtaskAsCSVLine = subtaskToCSVLine(subtask);
        subtaskRepository.create(subtask);
        subtask = new Subtask(1, "subtask", "description", Status.IN_PROGRESS);
        java.lang.String updatedSubtaskAsCSVLine = subtaskToCSVLine(subtask);

        java.lang.String lineBeforeUpdate = Files.readAllLines(pathToFile, StandardCharsets.UTF_8).getLast();
        subtaskRepository.update(subtask);
        java.lang.String lineAfterUpdate = Files.readAllLines(pathToFile, StandardCharsets.UTF_8).getLast();

        assertEquals(subtaskAsCSVLine, lineBeforeUpdate);
        assertEquals(updatedSubtaskAsCSVLine, lineAfterUpdate);
    }

    @Test
    void shouldDeleteAllSubtasksFromFile() throws IOException {
        Subtask subtask_1 = new Subtask(1, "subtask 1", "description", Status.NEW);
        Subtask subtask_2 = new Subtask(2, "subtask 2", "description", Status.NEW);
        List<java.lang.String> subtasksAsCSVLines = List.of(subtaskToCSVLine(subtask_1), subtaskToCSVLine(subtask_2));
        subtaskRepository.create(subtask_1);
        subtaskRepository.create(subtask_2);

        List<java.lang.String> fileLinesBeforeDelete = Files.readAllLines(pathToFile, StandardCharsets.UTF_8);
        subtaskRepository.deleteAll();
        List<java.lang.String> fileLinesAfterDelete = Files.readAllLines(pathToFile, StandardCharsets.UTF_8);

        assertTrue(fileLinesBeforeDelete.containsAll(subtasksAsCSVLines));
        assertFalse(fileLinesAfterDelete.containsAll(subtasksAsCSVLines));
    }

    @Test
    void shouldDeleteSubtaskByIntegerId() throws IOException {
        Subtask subtask_1 = new Subtask(1, "subtask 1", "description", Status.NEW);
        Subtask subtask_2 = new Subtask(2, "subtask 2", "description", Status.NEW);
        List<java.lang.String> subtasksAsCSVLines = List.of(subtaskToCSVLine(subtask_1), subtaskToCSVLine(subtask_2));
        subtaskRepository.create(subtask_1);
        subtaskRepository.create(subtask_2);

        List<java.lang.String> fileLinesBeforeDelete = Files.readAllLines(pathToFile, StandardCharsets.UTF_8);
        subtaskRepository.deleteById(1);
        List<java.lang.String> fileLinesAfterDelete = Files.readAllLines(pathToFile, StandardCharsets.UTF_8);

        assertTrue(fileLinesBeforeDelete.containsAll(subtasksAsCSVLines));
        assertTrue(fileLinesAfterDelete.contains(subtaskToCSVLine(subtask_2)));
        assertFalse(fileLinesAfterDelete.contains(subtaskToCSVLine(subtask_1)));
    }

    @Test
    void shouldGetAllSubtasksFromFile() {
        Subtask subtask_1 = new Subtask(1, "subtask 1", "description", Status.NEW);
        Subtask subtask_2 = new Subtask(2, "subtask 2", "description", Status.NEW);
        subtaskRepository.create(subtask_1);
        subtaskRepository.create(subtask_2);

        List<Subtask> subtaskList = subtaskRepository.getAll();

        assertEquals(2, subtaskList.size());
        assertTrue(subtaskList.contains(subtask_1));
        assertTrue(subtaskList.contains(subtask_2));
    }

    @Test
    void shouldGetNotEmptyOptionalWithSubtaskByIdFromFile() {
        Subtask subtask_1 = new Subtask(1, "subtask 1", "description", Status.NEW);
        Subtask subtask_2 = new Subtask(2, "subtask 2", "description", Status.NEW);
        subtaskRepository.create(subtask_1);
        subtaskRepository.create(subtask_2);

        Optional<Subtask> optionalSubtask_1 = subtaskRepository.getById(1);
        Optional<Subtask> optionalSubtask_2 = subtaskRepository.getById(2);

        assertTrue(optionalSubtask_1.isPresent());
        assertTrue(optionalSubtask_2.isPresent());
        assertEquals(subtask_1, optionalSubtask_1.get());
        assertEquals(subtask_2, optionalSubtask_2.get());
    }

    @Test
    void shouldGetEmptyOptionalWhenSubtaskAbsentInFile() {

        Optional<Subtask> optionalSubtask = subtaskRepository.getById(1);

        assertTrue(optionalSubtask.isEmpty());
    }
}