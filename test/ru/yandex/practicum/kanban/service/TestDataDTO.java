package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.TaskDTO;

public class TestDataDTO {

    public static TaskDTO TASK_1 = new TaskDTO("Task 1", "Task 1 for test", "NEW");
    public static TaskDTO TASK_2 = new TaskDTO("Task 2", "Task 2 for test", "IN_PROGRESS");
    public static TaskDTO SUBTASK_1 = new TaskDTO("Subtask 1", "Subtask 1 for test", "NEW");
    public static TaskDTO SUBTASK_2 = new TaskDTO("Subtask 2", "Subtask 2 for test", "NEW");
    public static TaskDTO EPIC_1 = new TaskDTO("Epic 1", "Epic 1 for test", "NEW");
    public static TaskDTO EPIC_2 = new TaskDTO("Epic 2", "Epic 1 for test", "NEW");

    public static TaskDTO getWithID(int id) {
        TaskDTO result = new TaskDTO();
        result.setId(id);
        return result;
    }

    public static TaskDTO getWithIDandUpdatedStatus(int id, Status status) {
        TaskDTO result = new TaskDTO();
        result.setId(id);
        result.setStatus(status.toString());
        return result;
    }

    public static TaskDTO getDatedTask() {
        return new TaskDTO("Task",
                "With startTime and duration",
                "NEW",
                "00:00 01.01.2025", 60);
    }

    public static TaskDTO getDatedSubtask() {
        return new TaskDTO("Subtask",
                "With startTime and duration",
                "NEW",
                "00:00 02.01.2025", 60);
    }

    public static TaskDTO getDatedEpic() {
        return new TaskDTO("Epic",
                "With startTime and duration",
                "NEW",
                "00:00 03.01.2025", 60);
    }
}