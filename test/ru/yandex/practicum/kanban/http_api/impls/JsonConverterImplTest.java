package ru.yandex.practicum.kanban.http_api.impls;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonConverterImplTest {

    private final JsonConverterImpl jsonConverter = new JsonConverterImpl(true);

    @Test
    void canConvertTask() {
        String json = """
                {
                  "type": "task",
                  "id": 1,
                  "title": "Task",
                  "description": "-",
                  "status": "NEW",
                  "startTime": "09:36 27.01.2025",
                  "endTime": "10:36 27.01.2025",
                  "durationInMinutes": 60
                }""";
        LocalDateTime starTime = LocalDateTime.parse("09:36 27.01.2025", Task.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);

        Task task = new Task(1, "Task", "-", Status.NEW, starTime, duration);

        assertEquals(json, jsonConverter.convert(task));
    }

    @Test
    void canConvertSubtaskWithoutEpic() {
        String json = """
                {
                  "type": "subtask",
                  "id": 1,
                  "title": "Task",
                  "description": "-",
                  "status": "NEW",
                  "startTime": "09:36 27.01.2025",
                  "endTime": "10:36 27.01.2025",
                  "durationInMinutes": 60,
                  "epic_id": null
                }""";
        LocalDateTime starTime = LocalDateTime.parse("09:36 27.01.2025", Task.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);

        Subtask subtask = new Subtask(1, "Task", "-", Status.NEW, starTime, duration);

        assertEquals(json, jsonConverter.convert(subtask));
    }

    @Test
    void canConvertSubtaskWithEpic() {
        String json = """
                {
                  "type": "subtask",
                  "id": 1,
                  "title": "Task",
                  "description": "-",
                  "status": "NEW",
                  "startTime": "09:36 27.01.2025",
                  "endTime": "10:36 27.01.2025",
                  "durationInMinutes": 60,
                  "epic_id": 2
                }""";
        LocalDateTime starTime = LocalDateTime.parse("09:36 27.01.2025", Task.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);

        Subtask subtask = new Subtask(1, "Task", "-", Status.NEW, starTime, duration);
        Epic epic = new Epic(2, "Epic", "-", Status.NEW, starTime, duration);
        subtask.addEpic(epic);

        assertEquals(json, jsonConverter.convert(subtask));
    }

    @Test
    void canConvertEpicWithoutSubtask() {
        String json = """
                {
                  "type": "epic",
                  "id": 1,
                  "title": "Epic",
                  "description": "-",
                  "status": "NEW",
                  "startTime": "09:36 27.01.2025",
                  "endTime": "10:36 27.01.2025",
                  "durationInMinutes": 60,
                  "subtasksId": []
                }""";
        LocalDateTime starTime = LocalDateTime.parse("09:36 27.01.2025", Task.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);

        Epic epic = new Epic(1, "Epic", "-", Status.NEW, starTime, duration);

        assertEquals(json, jsonConverter.convert(epic));
    }

    @Test
    void canConvertEpicWithSubtask() {
        String json = """
                {
                  "type": "epic",
                  "id": 1,
                  "title": "Epic",
                  "description": "-",
                  "status": "NEW",
                  "startTime": "09:36 27.01.2025",
                  "endTime": "10:36 27.01.2025",
                  "durationInMinutes": 60,
                  "subtasksId": [2,3]
                }""";
        LocalDateTime starTime = LocalDateTime.parse("09:36 27.01.2025", Task.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);

        Epic epic = new Epic(1, "Epic", "-", Status.NEW, starTime, duration);
        Subtask subtask_1 = new Subtask(2, "Subtask", "-", Status.NEW);
        Subtask subtask_2 = new Subtask(3, "Subtask", "-", Status.NEW);
        epic.addSubtask(subtask_1);
        epic.addSubtask(subtask_2);

        assertEquals(json, jsonConverter.convert(epic));
    }

    @Test
    void canConvertTaskCollection() {
        String json = """
                [
                  {
                    "type": "task",
                    "id": 1,
                    "title": "Task",
                    "description": "-",
                    "status": "NEW",
                    "startTime": "09:36 27.01.2025",
                    "endTime": "10:36 27.01.2025",
                    "durationInMinutes": 60
                  },
                  {
                    "type": "task",
                    "id": 2,
                    "title": "Task",
                    "description": "-",
                    "status": "NEW",
                    "startTime": "09:36 27.01.2025",
                    "endTime": "10:36 27.01.2025",
                    "durationInMinutes": 60
                  }
                ]""";

        LocalDateTime starTime = LocalDateTime.parse("09:36 27.01.2025", Task.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);

        Task task1 = new Task(1, "Task", "-", Status.NEW, starTime, duration);
        Task task2 = new Task(2, "Task", "-", Status.NEW, starTime, duration);

        assertEquals(json, jsonConverter.convert(List.of(task1, task2)));
    }

    @Test
    void canConvertSubtaskCollection() {
        String json = """
                [
                  {
                    "type": "subtask",
                    "id": 1,
                    "title": "Subtask",
                    "description": "-",
                    "status": "NEW",
                    "startTime": "09:36 27.01.2025",
                    "endTime": "10:36 27.01.2025",
                    "durationInMinutes": 60,
                    "epic_id": null
                  },
                  {
                    "type": "subtask",
                    "id": 2,
                    "title": "Subtask",
                    "description": "-",
                    "status": "NEW",
                    "startTime": "09:36 27.01.2025",
                    "endTime": "10:36 27.01.2025",
                    "durationInMinutes": 60,
                    "epic_id": null
                  }
                ]""";

        LocalDateTime starTime = LocalDateTime.parse("09:36 27.01.2025", Task.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);

        Subtask subtask1 = new Subtask(1, "Subtask", "-", Status.NEW, starTime, duration);
        Subtask subtask2 = new Subtask(2, "Subtask", "-", Status.NEW, starTime, duration);

        assertEquals(json, jsonConverter.convert(List.of(subtask1, subtask2)));
    }

    @Test
    void canConvertEpicCollection() {
        String json = """
                [
                  {
                    "type": "epic",
                    "id": 2,
                    "title": "Epic",
                    "description": "-",
                    "status": "NEW",
                    "startTime": "15:42 29.01.2025",
                    "endTime": "16:42 29.01.2025",
                    "durationInMinutes": 60,
                    "subtasksId": [1,3]
                  }
                ]""";
        LocalDateTime starTime = LocalDateTime.parse("15:42 29.01.2025", Task.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(60);

        Epic epic = new Epic(2, "Epic", "-", Status.NEW, starTime, duration);
        Subtask subtask1 = new Subtask(1, "Task", "-", Status.NEW);
        Subtask subtask2 = new Subtask(3, "Task", "-", Status.NEW);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        assertEquals(json, jsonConverter.convert(List.of(epic)));
    }

    @Test
    void canConvertJsonToTaskDTO() {
        TaskDTO taskDTO = new TaskDTO(1, "Task", "-", "NEW", "09:36 27.01.2025", 60);
        String json = """
                  {
                  "type": "task",
                  "id": 1,
                  "title": "Task",
                  "description": "-",
                  "status": "NEW",
                  "startTime": "09:36 27.01.2025",
                  "endTime": "10:36 27.01.2025",
                  "durationInMinutes": 60
                }
                """;

        assertEquals(taskDTO, jsonConverter.convertToObject(json));
    }

    @Test
    void canConvertJsonArrayToListTaskDTO() {
        TaskDTO task_1 = new TaskDTO(1, "Task 1", "-", "NEW", "09:36 27.01.2025", 60);
        TaskDTO task_2 = new TaskDTO("Task 2", "-", "NEW");
        String jsonArray = """
                [
                {
                "id": 1,
                "title": "Task 1",
                "description": "-",
                "status": "NEW",
                "startTime": "09:36 27.01.2025",
                "durationInMinutes": 60
                },
                {
                "type": "task",
                "title": "Task 2",
                "description": "-",
                "status": "NEW"
                }
                ]""";
        List<TaskDTO> list = jsonConverter.convertToList(jsonArray);

        assertTrue(list.contains(task_1));
        assertTrue(list.contains(task_2));
    }
}