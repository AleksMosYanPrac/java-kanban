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

class InFileEpicRepositoryWithCSVFileDataSourceTest {

    private Path pathToFile;
    private InFileEpicRepositoryImpl epicRepository;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        try {
            pathToFile = tempDir.resolve("temp_file_1.csv");
            Files.createFile(pathToFile);
            this.epicRepository = new InFileEpicRepositoryImpl(new CSVFileDataSource(pathToFile));
        } catch (InvalidPathException ipe) {
            System.err.println(
                    "error creating temporary temp_file_1.csv in " +
                            this.getClass().getSimpleName());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Test
    void shouldAppendEpicWithoutSubtasksToFileAsNewLastLine() throws IOException {
        Epic epic = EPIC_1;
        String epicAsCSVLine = epicToCSVLine(epic);

        epicRepository.create(epic);
        String lastLineInFile = Files.readAllLines(pathToFile, StandardCharsets.UTF_8).getLast();

        assertEquals(epicAsCSVLine, lastLineInFile);
    }

    @Test
    void shouldAppendEpicWithSubtasksToFileAsNewLastLine() throws IOException {
        Epic epic = EPIC_1;
        Subtask subtask = SUBTASK_1;
        epic.addSubtask(subtask);
        String epicAsCSVLine = epicToCSVLine(epic);

        epicRepository.create(epic);
        String lastLineInFile = Files.readAllLines(pathToFile, StandardCharsets.UTF_8).getLast();

        assertEquals(epicAsCSVLine, lastLineInFile);
    }

    @Test
    void shouldOverwriteStringLineInFileForUpdatedEpic() throws IOException {
        Epic epic = EPIC_1;
        String epicAsCSVLine = epicToCSVLine(epic);
        epicRepository.create(epic);
        epic.addSubtask(SUBTASK_1);
        String updatedEpicAsCSVLine = epicToCSVLine(epic);

        String lineBeforeUpdate = Files.readAllLines(pathToFile, StandardCharsets.UTF_8).getLast();
        epicRepository.update(epic);
        String lineAfterUpdate = Files.readAllLines(pathToFile, StandardCharsets.UTF_8).getLast();

        assertEquals(epicAsCSVLine, lineBeforeUpdate);
        assertEquals(updatedEpicAsCSVLine, lineAfterUpdate);
    }

    @Test
    void shouldDeleteAllEpicsFromFile() throws IOException {
        Epic epic_1 = new Epic(1, "epic 1", "description", Status.NEW);
        Epic epic_2 = new Epic(2, "epic 2", "description", Status.NEW);
        List<String> epicsAsCSVLines = List.of(epicToCSVLine(epic_1), epicToCSVLine(epic_2));
        epicRepository.create(epic_1);
        epicRepository.create(epic_2);

        List<String> fileLinesBeforeDelete = Files.readAllLines(pathToFile, StandardCharsets.UTF_8);
        epicRepository.deleteAll();
        List<String> fileLinesAfterDelete = Files.readAllLines(pathToFile, StandardCharsets.UTF_8);

        assertTrue(fileLinesBeforeDelete.containsAll(epicsAsCSVLines));
        assertFalse(fileLinesAfterDelete.containsAll(epicsAsCSVLines));
    }

    @Test
    void shouldDeleteEpicByIntegerId() throws IOException {
        Epic epic_1 = new Epic(1, "epic 1", "description", Status.NEW);
        Epic epic_2 = new Epic(2, "epic 2", "description", Status.NEW);
        List<String> epicsAsCSVLines = List.of(epicToCSVLine(epic_1), epicToCSVLine(epic_2));
        epicRepository.create(epic_1);
        epicRepository.create(epic_2);

        List<String> fileLinesBeforeDelete = Files.readAllLines(pathToFile, StandardCharsets.UTF_8);
        epicRepository.deleteById(1);
        List<String> fileLinesAfterDelete = Files.readAllLines(pathToFile, StandardCharsets.UTF_8);

        assertTrue(fileLinesBeforeDelete.containsAll(epicsAsCSVLines));
        assertTrue(fileLinesAfterDelete.contains(epicToCSVLine(epic_2)));
        assertFalse(fileLinesAfterDelete.contains(epicToCSVLine(epic_1)));
    }

    @Test
    void shouldGetAllEpicFromFile() {
        Epic epic_1 = new Epic(1, "epic 1", "description", Status.NEW);
        Epic epic_2 = new Epic(2, "epic 2", "description", Status.NEW);
        epicRepository.create(epic_1);
        epicRepository.create(epic_2);

        List<Epic> epicList = epicRepository.getAll();

        assertEquals(2,epicList.size());
        assertTrue(epicList.contains(epic_1));
        assertTrue(epicList.contains(epic_2));
    }

    @Test
    void shouldGetNotEmptyOptionalWithEpicByIdFromFile() {
        Epic epic_1 = new Epic(1, "epic 1", "description", Status.NEW);
        Epic epic_2 = new Epic(2, "epic 2", "description", Status.NEW);
        epicRepository.create(epic_1);
        epicRepository.create(epic_2);

        Optional<Epic> optionalEpic_1 = epicRepository.getById(1);
        Optional<Epic> optionalEpic_2 = epicRepository.getById(2);

        assertTrue(optionalEpic_1.isPresent());
        assertTrue(optionalEpic_2.isPresent());
        assertEquals(epic_1,optionalEpic_1.get());
        assertEquals(epic_2,optionalEpic_2.get());
    }

    @Test
    void shouldGetEmptyOptionalWhenEpicAbsentInFile() {

        Optional<Epic> optionalEpic = epicRepository.getById(1);

        assertTrue(optionalEpic.isEmpty());
    }
}