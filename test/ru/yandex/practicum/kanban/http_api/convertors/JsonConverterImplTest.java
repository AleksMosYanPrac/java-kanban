package ru.yandex.practicum.kanban.http_api.convertors;

import com.google.gson.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.*;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class JsonConverterImplTest {

    private final JsonConverterImpl jsonConverter = new JsonConverterImpl(false);
    private static List<String> listJsonObjects = new ArrayList<>();
    private static List<String> listJsonArrays = new ArrayList<>();

    @BeforeAll
    static void parseURI() throws Exception {
        URI uri = Objects.requireNonNull(
                JsonConverterImplTest.class.getClassLoader().getResource("resources/testData.json")).toURI();
        String data = Files.readString(Path.of(uri), StandardCharsets.UTF_8);
        JsonElement element = JsonParser.parseString(data);
        JsonArray jsonArray = element.getAsJsonArray();
        for (JsonElement el : jsonArray) {
            if (el.isJsonArray()) {
                listJsonArrays.add(el.getAsJsonArray().toString());
                JsonArray innerArray = el.getAsJsonArray();
                for (JsonElement innerElement : innerArray) {
                    if (innerElement.isJsonObject()) {
                        listJsonObjects.add(innerElement.getAsJsonObject().toString());
                    }
                }
            }
        }
    }

    @Test
    void canConvertTask() {
        LocalDateTime starTime = LocalDateTime.parse("09:36 27.01.2025", Task.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);

        Task task = new Task(1, "Task", "-", Status.NEW, starTime, duration);

        assertTrue(listJsonObjects.contains(jsonConverter.convert(task)));
    }

    @Test
    void canConvertSubtaskWithoutEpic() {
        LocalDateTime starTime = LocalDateTime.parse("09:36 27.01.2025", Task.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);

        Subtask subtask = new Subtask(1, "Task", "-", Status.NEW, starTime, duration);

        assertTrue(listJsonObjects.contains(jsonConverter.convert(subtask)));
    }

    @Test
    void canConvertSubtaskWithEpic() {
        LocalDateTime starTime = LocalDateTime.parse("09:36 27.01.2025", Task.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);

        Subtask subtask = new Subtask(1, "Task", "-", Status.NEW, starTime, duration);
        Epic epic = new Epic(2, "Epic", "-", Status.NEW, starTime, duration);
        subtask.addEpic(epic);

        assertTrue(listJsonObjects.contains(jsonConverter.convert(subtask)));
    }

    @Test
    void canConvertEpicWithoutSubtask() {
        LocalDateTime starTime = LocalDateTime.parse("09:36 27.01.2025", Task.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);

        Epic epic = new Epic(1, "Epic", "-", Status.NEW, starTime, duration);

        assertTrue(listJsonObjects.contains(jsonConverter.convert(epic)));
    }

    @Test
    void canConvertEpicWithSubtask() {
        LocalDateTime starTime = LocalDateTime.parse("09:36 27.01.2025", Task.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);

        Epic epic = new Epic(1, "Epic", "-", Status.NEW, starTime, duration);
        Subtask subtask_1 = new Subtask(2, "Subtask", "-", Status.NEW);
        Subtask subtask_2 = new Subtask(3, "Subtask", "-", Status.NEW);
        epic.addSubtask(subtask_1);
        epic.addSubtask(subtask_2);

        assertTrue(listJsonObjects.contains(jsonConverter.convert(epic)));
    }

    @Test
    void canConvertTaskCollection() {
        LocalDateTime starTime = LocalDateTime.parse("09:36 27.01.2025", Task.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);

        Task task1 = new Task(1, "Task", "-", Status.NEW, starTime, duration);
        Task task2 = new Task(2, "Task", "-", Status.NEW, starTime, duration);

        assertTrue(listJsonArrays.contains(jsonConverter.convert(List.of(task1, task2))));
    }

    @Test
    void canConvertSubtaskCollection() {
        LocalDateTime starTime = LocalDateTime.parse("09:36 27.01.2025", Task.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);

        Subtask subtask1 = new Subtask(1, "Subtask", "-", Status.NEW, starTime, duration);
        Subtask subtask2 = new Subtask(2, "Subtask", "-", Status.NEW, starTime, duration);

        assertTrue(listJsonArrays.contains(jsonConverter.convert(List.of(subtask1, subtask2))));
    }

    @Test
    void canConvertEpicCollection() {
        LocalDateTime starTime = LocalDateTime.parse("15:42 29.01.2025", Task.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);

        Epic epic = new Epic(2, "Epic", "-", Status.NEW, starTime, duration);
        Subtask subtask1 = new Subtask(1, "Task", "-", Status.NEW);
        Subtask subtask2 = new Subtask(3, "Task", "-", Status.NEW);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        assertTrue(listJsonArrays.contains(jsonConverter.convert(List.of(epic))));
    }

    @Test
    void canConvertJsonToTaskDTO() {
        TaskDTO taskDTO = new TaskDTO(1, "Task", "-", "NEW", "09:36 27.01.2025", 60);

        assertEquals(taskDTO, jsonConverter.convertToObject(listJsonObjects.get(0)));
    }

    @Test
    void canConvertJsonArrayToListTaskDTO() {

        TaskDTO taskDTO = new TaskDTO(2, "Epic", "-", "NEW", "15:42 29.01.2025", 60);

        assertTrue(jsonConverter.convertToList(listJsonArrays.getLast()).contains(taskDTO));
    }
}