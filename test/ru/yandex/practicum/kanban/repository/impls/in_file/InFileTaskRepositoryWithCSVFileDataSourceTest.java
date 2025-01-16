package ru.yandex.practicum.kanban.repository.impls.in_file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.Task;
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

class InFileTaskRepositoryWithCSVFileDataSourceTest {

    private Path pathToFile;
    private InFileTaskRepositoryImpl taskRepository;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws InvalidPathException, IOException {
        pathToFile = tempDir.resolve("temp_file_1.csv");
        Files.createFile(pathToFile);
        this.taskRepository = new InFileTaskRepositoryImpl(new CSVFileDataSource(pathToFile));
    }

    @Test
    void shouldAppendTaskToFileAsNewLastLine() throws IOException {
        Task task = TASK_1;
        java.lang.String taskAsCSVLine = taskToCSVLine(task);

        taskRepository.create(task);
        java.lang.String lastLineInFile = Files.readAllLines(pathToFile, StandardCharsets.UTF_8).getLast();

        assertEquals(taskAsCSVLine, lastLineInFile);
    }

    @Test
    void shouldOverwriteStringLineInFileForUpdatedTask() throws IOException {
        Task task = new Task(1, "task", "description", Status.NEW);
        java.lang.String taskAsCSVLine = taskToCSVLine(task);
        taskRepository.create(task);
        task = new Task(1, "task", "description", Status.IN_PROGRESS);
        java.lang.String updatedTaskAsCSVLine = taskToCSVLine(task);

        java.lang.String lineBeforeUpdate = Files.readAllLines(pathToFile, StandardCharsets.UTF_8).getLast();
        taskRepository.update(task);
        java.lang.String lineAfterUpdate = Files.readAllLines(pathToFile, StandardCharsets.UTF_8).getLast();

        assertEquals(taskAsCSVLine, lineBeforeUpdate);
        assertEquals(updatedTaskAsCSVLine, lineAfterUpdate);
    }

    @Test
    void shouldDeleteAllTasksFromFile() throws IOException {
        Task task_1 = new Task(1, "task 1", "description", Status.NEW);
        Task task_2 = new Task(2, "task 2", "description", Status.NEW);
        List<java.lang.String> tasksAsCSVLines = List.of(taskToCSVLine(task_1), taskToCSVLine(task_2));
        taskRepository.create(task_1);
        taskRepository.create(task_2);

        List<java.lang.String> fileLinesBeforeDelete = Files.readAllLines(pathToFile, StandardCharsets.UTF_8);
        taskRepository.deleteAll();
        List<java.lang.String> fileLinesAfterDelete = Files.readAllLines(pathToFile, StandardCharsets.UTF_8);

        assertTrue(fileLinesBeforeDelete.containsAll(tasksAsCSVLines));
        assertFalse(fileLinesAfterDelete.containsAll(tasksAsCSVLines));
    }

    @Test
    void shouldDeleteTaskByIntegerId() throws IOException {
        Task task_1 = new Task(1, "task 1", "description", Status.NEW);
        Task task_2 = new Task(2, "task 2", "description", Status.NEW);
        List<java.lang.String> tasksAsCSVLines = List.of(taskToCSVLine(task_1), taskToCSVLine(task_2));
        taskRepository.create(task_1);
        taskRepository.create(task_2);

        List<java.lang.String> fileLinesBeforeDelete = Files.readAllLines(pathToFile, StandardCharsets.UTF_8);
        taskRepository.deleteById(1);
        List<java.lang.String> fileLinesAfterDelete = Files.readAllLines(pathToFile, StandardCharsets.UTF_8);

        assertTrue(fileLinesBeforeDelete.containsAll(tasksAsCSVLines));
        assertTrue(fileLinesAfterDelete.contains(taskToCSVLine(task_2)));
        assertFalse(fileLinesAfterDelete.contains(taskToCSVLine(task_1)));
    }

    @Test
    void shouldGetAllTaskFromFile() {
        Task task_1 = new Task(1, "task 1", "description", Status.NEW);
        Task task_2 = new Task(2, "task 2", "description", Status.NEW);
        taskRepository.create(task_1);
        taskRepository.create(task_2);

        List<Task> taskList = taskRepository.getAll();

        assertEquals(2, taskList.size());
        assertTrue(taskList.contains(task_1));
        assertTrue(taskList.contains(task_2));
    }

    @Test
    void shouldGetNotEmptyOptionalWithTaskByIdFromFile() {
        Task task_1 = new Task(1, "task 1", "description", Status.NEW);
        Task task_2 = new Task(2, "task 2", "description", Status.NEW);
        taskRepository.create(task_1);
        taskRepository.create(task_2);

        Optional<Task> optionalTask_1 = taskRepository.getById(1);
        Optional<Task> optionalTask_2 = taskRepository.getById(2);

        assertTrue(optionalTask_1.isPresent());
        assertTrue(optionalTask_2.isPresent());
        assertEquals(task_1, optionalTask_1.get());
        assertEquals(task_2, optionalTask_2.get());
    }

    @Test
    void shouldGetEmptyOptionalWhenTaskAbsentInFile() {

        Optional<Task> optionalTask = taskRepository.getById(1);

        assertTrue(optionalTask.isEmpty());
    }
}