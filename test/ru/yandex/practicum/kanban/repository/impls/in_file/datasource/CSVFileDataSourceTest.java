package ru.yandex.practicum.kanban.repository.impls.in_file.datasource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.repository.TestData.*;

class CSVFileDataSourceTest {
    private final String COLUMN_LABELS_LINE = "id,type,title,status,description,epic,subtasks";

    private Path pathToEmptyFile;
    private Path pathToDataFile;
    private CSVFileDataSource dataSource;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        try {
            pathToEmptyFile = tempDir.resolve("temp_file_1.csv");
            pathToDataFile = tempDir.resolve("temp_file_2.csv");
            Files.createFile(pathToEmptyFile);
            Files.createFile(pathToDataFile);
        } catch (InvalidPathException ipe) {
            System.err.println(
                    "error creating temporary file in " +
                            this.getClass().getSimpleName());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Test
    void canWriteDataSetAsCSVLineWithColumnLabelsToEmptyFile() throws IOException {
        DataSet dataSet = taskToDataSet(TASK_1);
        String lineWithData = taskToCSVLine(TASK_1);
        DataQuery typeQuery = new DataQuery("TYPE", "TASK");
        this.dataSource = new CSVFileDataSource(pathToEmptyFile);

        boolean isFileEmptyBeforeWriteData = Files.readAllLines(pathToEmptyFile, StandardCharsets.UTF_8).isEmpty();
        dataSource.write(dataSet, typeQuery);
        List<String> linesFromFile = Files.readAllLines(pathToEmptyFile, StandardCharsets.UTF_8);

        assertTrue(isFileEmptyBeforeWriteData);
        assertEquals(2, linesFromFile.size());
        assertEquals(COLUMN_LABELS_LINE, linesFromFile.getFirst());
        assertEquals(lineWithData, linesFromFile.getLast());
    }

    @Test
    void canWriteDataSetAsCSVLineToDataFile() throws IOException {
        DataSet dataSet = taskToDataSet(TASK_1);
        String lineWithData = taskToCSVLine(TASK_1);
        DataQuery taskQuery = new DataQuery("TYPE", "TASK");
        Files.writeString(pathToDataFile, COLUMN_LABELS_LINE);
        this.dataSource = new CSVFileDataSource(pathToDataFile);

        boolean isFileContainsAnyData = Files.readAllLines(pathToDataFile, StandardCharsets.UTF_8).isEmpty();
        dataSource.write(dataSet, taskQuery);
        List<String> linesFromFile = Files.readAllLines(pathToDataFile, StandardCharsets.UTF_8);

        assertFalse(isFileContainsAnyData);
        assertEquals(2, linesFromFile.size());
        assertEquals(COLUMN_LABELS_LINE, linesFromFile.getFirst());
        assertEquals(lineWithData, linesFromFile.getLast());
    }

    @Test
    void canOverwriteCSVLineInDataFileByNewDataSet() throws IOException {
        Task task = new Task(1, "task", "description", Status.NEW);
        Task updatedTask = new Task(1, "task", "description", Status.IN_PROGRESS);
        String lineWithTask = taskToCSVLine(task);
        String lineWithUpdatedTask = taskToCSVLine(updatedTask);
        DataSet updatedData = taskToDataSet(updatedTask);
        DataQuery typeQuery = new DataQuery("TYPE", "TASK");
        DataQuery idQuery = new DataQuery("ID", Integer.toString(updatedTask.getId()));
        Files.writeString(pathToDataFile, COLUMN_LABELS_LINE + "\n", StandardOpenOption.APPEND);
        Files.writeString(pathToDataFile, lineWithTask, StandardOpenOption.APPEND);
        this.dataSource = new CSVFileDataSource(pathToDataFile);

        List<String> linesBeforeOverwrite = Files.readAllLines(pathToDataFile, StandardCharsets.UTF_8);
        dataSource.overwrite(updatedData, typeQuery, idQuery);
        List<String> linesAfterOverwrite = Files.readAllLines(pathToDataFile, StandardCharsets.UTF_8);

        assertEquals(2, linesBeforeOverwrite.size());
        assertEquals(COLUMN_LABELS_LINE, linesBeforeOverwrite.getFirst());
        assertEquals(lineWithTask, linesBeforeOverwrite.getLast());
        assertEquals(2, linesAfterOverwrite.size());
        assertEquals(COLUMN_LABELS_LINE, linesAfterOverwrite.getFirst());
        assertEquals(lineWithUpdatedTask, linesAfterOverwrite.getLast());
    }

    @Test
    void canDeleteCSVLineInDataFileByQuery() throws IOException {
        DataQuery typeQuery = new DataQuery("TYPE", "TASK");
        String lineWithTask_1 = taskToCSVLine(TASK_1);
        String lineWithTask_2 = taskToCSVLine(TASK_2);
        Files.writeString(pathToDataFile, COLUMN_LABELS_LINE + "\n", StandardOpenOption.APPEND);
        Files.writeString(pathToDataFile, lineWithTask_1 + "\n", StandardOpenOption.APPEND);
        Files.writeString(pathToDataFile, lineWithTask_2 + "\n", StandardOpenOption.APPEND);
        this.dataSource = new CSVFileDataSource(pathToDataFile);

        List<String> linesBeforeClear = Files.readAllLines(pathToDataFile, StandardCharsets.UTF_8);
        dataSource.clear(typeQuery);
        List<String> linesAfterClear = Files.readAllLines(pathToDataFile, StandardCharsets.UTF_8);

        assertEquals(3, linesBeforeClear.size());
        assertEquals(COLUMN_LABELS_LINE, linesBeforeClear.getFirst());
        assertEquals(lineWithTask_2, linesBeforeClear.getLast());
        assertEquals(1, linesAfterClear.size());
        assertEquals(COLUMN_LABELS_LINE, linesAfterClear.getFirst());
    }

    @Test
    void canReadDataFromDataFileByQueryWithType() throws IOException {
        DataQuery typeQuery = new DataQuery("TYPE", "TASK");
        String lineWithTask_1 = taskToCSVLine(TASK_1);
        DataSet dataSet = taskToDataSet(TASK_1);
        Files.writeString(pathToDataFile, COLUMN_LABELS_LINE + "\n", StandardOpenOption.APPEND);
        Files.writeString(pathToDataFile, lineWithTask_1 + "\n", StandardOpenOption.APPEND);
        this.dataSource = new CSVFileDataSource(pathToDataFile);

        List<String> linesFromFile = Files.readAllLines(pathToDataFile, StandardCharsets.UTF_8);
        List<DataSet> result = dataSource.read(typeQuery);

        assertEquals(2, linesFromFile.size());
        assertEquals(COLUMN_LABELS_LINE, linesFromFile.getFirst());
        assertEquals(lineWithTask_1, linesFromFile.getLast());
        assertEquals(1, result.size());
    }
}