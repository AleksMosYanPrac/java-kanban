package ru.yandex.practicum.kanban.repository;

import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.repository.impls.in_file.datasource.DataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TestData {

    public static Task TASK_1 = new Task(1, "task", "description", Status.NEW);
    public static Task TASK_2 = new Task(4, "task", "description", Status.NEW);
    public static Epic EPIC_1 = new Epic(3, "epic", "description", Status.NEW);
    public static Subtask SUBTASK_1 = new Subtask(2, "subtask", "description", Status.NEW);
    public static Subtask SUBTASK_2 = new Subtask(5, "subtask", "description", Status.NEW);

    public static List<Task> getListTasks() {
        List<Task> listTasks = new ArrayList<>();
        listTasks.add(TASK_1);
        listTasks.add(SUBTASK_1);
        listTasks.add(EPIC_1);
        listTasks.add(TASK_2);
        listTasks.add(SUBTASK_2);
        return listTasks;
    }

    public static DataSet taskToDataSet(Task task) {
        return DataSet.builder()
                .add("id", task.getId())
                .add("title", task.getTitle())
                .add("description", task.getDescription())
                .add("status", task.getStatus().toString())
                .add("start_time", getStringStartTime(task))
                .add("duration", getStringDuration(task))
                .build();
    }

    public static DataSet epicToDataSet(Epic epic) {
        StringBuilder idOfSubtasks = new StringBuilder();
        List<Subtask> subtasks = epic.getSubtasks();
        idOfSubtasks.append("[");
        for (Subtask subtask : subtasks) {
            idOfSubtasks.append(subtask.getId());
            if (subtasks.indexOf(subtask) < subtasks.size() - 1) {
                idOfSubtasks.append(",");
            }
            idOfSubtasks.append("]");
        }
        return DataSet.builder()
                .add("id", epic.getId())
                .add("title", epic.getTitle())
                .add("description", epic.getDescription())
                .add("status", epic.getStatus().toString())
                .add("start_time", getStringStartTime(epic))
                .add("duration", getStringDuration(epic))
                .add("subtasks", idOfSubtasks.toString())
                .build();
    }

    public static String taskToCSVLine(Task task) {
        return TaskBuilder.CSVParser.taskToCSVLine(task);
    }

    public static String epicToCSVLine(Epic epic) {
        return TaskBuilder.CSVParser.epicToCSVLine(epic);
    }

    public static String subtaskToCSVLine(Subtask subtask) {
        return TaskBuilder.CSVParser.subtaskToCSVLine(subtask);
    }

    private static String getStringStartTime(Task task) {
        String starTime = "";
        if (task.hasStartTimeAndDuration()) {
            starTime = task.getStartTime().format(Task.DATE_TIME_FORMATTER);
        }
        return starTime;
    }

    private static String getStringDuration(Task task) {
        String duration = "";
        if (task.hasStartTimeAndDuration()) {
            duration = Long.toString(task.getDuration().toMinutes());
        }
        return duration;
    }
}