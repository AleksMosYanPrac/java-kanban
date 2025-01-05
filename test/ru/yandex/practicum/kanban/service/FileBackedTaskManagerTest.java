package ru.yandex.practicum.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.repository.TestData;
import ru.yandex.practicum.kanban.service.util.Managers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.service.TestDataDTO.*;

public class FileBackedTaskManagerTest {

    private TaskManager taskManager;
    private Path pathToFile;
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws InvalidPathException, IOException {
        pathToFile = tempDir.resolve("temp_file_1.csv");
        Files.createFile(pathToFile);
        this.taskManager = Managers.fileBackedTaskManager(pathToFile);
    }

    @Test
    void canAddDifferentTypeTask() throws IOException {
        Task task = taskManager.createTask(TASK_1);
        Epic epic = taskManager.createEpic(EPIC_1);
        Subtask subtask = taskManager.createSubtask(SUBTASK_1);
        int linesWithData = 3;

        List<String> linesFromFile = Files.readAllLines(pathToFile, StandardCharsets.UTF_8);

        assertTrue(linesFromFile.contains(TestData.taskToCSVLine(task)));
        assertTrue(linesFromFile.contains(TestData.epicToCSVLine(epic)));
        assertTrue(linesFromFile.contains(TestData.subtaskToCSVLine(subtask)));
        assertEquals(linesWithData, linesFromFile.size());
    }

    @Test
    void shouldCreateEpicWithLinkedSubtask() throws IOException {
        int linesWithColumnLabels = 0;
        int linesWithData = 2;

        Epic epic = taskManager.createEpic(EPIC_1, SUBTASK_1);
        List<String> linesFromFile = Files.readAllLines(pathToFile, StandardCharsets.UTF_8);

        assertTrue(linesFromFile.contains(TestData.epicToCSVLine(epic)));
        assertTrue(linesFromFile.contains(TestData.subtaskToCSVLine(epic.getSubtasks().getFirst())));
        assertEquals(linesWithData + linesWithColumnLabels, linesFromFile.size());
    }

    @Test
    void shouldDeleteTask() throws IOException {
        Task task = taskManager.createTask(TASK_1);
        int linesWithColumnLabels = 0;
        int linesWithData = 1;

        List<String> linesBeforeDelete = Files.readAllLines(pathToFile, StandardCharsets.UTF_8);
        taskManager.deleteTask(getWithID(task.getId()));
        List<String> linesAfterDelete = Files.readAllLines(pathToFile, StandardCharsets.UTF_8);

        assertEquals(linesWithData + linesWithColumnLabels, linesBeforeDelete.size());
        assertTrue(linesAfterDelete.isEmpty());
    }

    @Test
    void shouldDeleteSubtaskWhenDeleteEpic() throws IOException {
        Epic addedEpic = taskManager.createEpic(EPIC_1, SUBTASK_1);
        int linesWithColumnLabels = 0;
        int linesWithData = 2;

        List<String> linesBeforeDelete = Files.readAllLines(pathToFile, StandardCharsets.UTF_8);
        taskManager.deleteEpic(getWithID(addedEpic.getId()));
        List<String> linesAfterDelete = Files.readAllLines(pathToFile, StandardCharsets.UTF_8);

        assertEquals(linesWithData + linesWithColumnLabels, linesBeforeDelete.size());
        assertTrue(linesAfterDelete.isEmpty());
    }

    @Test
    void shouldUnlinkDeletedSubtaskAndUpdateEpic() throws IOException {
        Epic epic = taskManager.createEpic(EPIC_1, SUBTASK_1);
        Subtask addedSubtask = epic.getSubtasks().getFirst();

        taskManager.deleteSubtask(getWithID(addedSubtask.getId()));
        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        List<String> linesAfterDelete = Files.readAllLines(pathToFile, StandardCharsets.UTF_8);

        assertTrue(linesAfterDelete.contains(TestData.epicToCSVLine(updatedEpic)));
        assertFalse(linesAfterDelete.contains(TestData.subtaskToCSVLine(addedSubtask)));
    }
}