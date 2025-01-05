package ru.yandex.practicum.kanban.repository;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
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
                .add("subtasks", idOfSubtasks.toString())
                .build();
    }

    public static String taskToCSVLine(Task task) {
        return String.format("%s,TASK,%s,%s,%s,,",
                task.getId(),
                task.getTitle(),
                task.getStatus().toString(),
                task.getDescription());
    }

    public static String epicToCSVLine(Epic epic) {
        StringBuilder idOfSubtasks = new StringBuilder();
        List<Subtask> subtasks = epic.getSubtasks();
        idOfSubtasks.append("[");
        for (Subtask subtask : subtasks) {
            idOfSubtasks.append(subtask.getId());
            if (subtasks.indexOf(subtask) < subtasks.size() - 1) {
                idOfSubtasks.append(",");
            }
        }
        idOfSubtasks.append("]");
        return String.format("%s,EPIC,%s,%s,%s,,%s",
                epic.getId(),
                epic.getTitle(),
                epic.getStatus().toString(),
                epic.getDescription(),
                idOfSubtasks.toString());
    }

    public static String subtaskToCSVLine(Subtask subtask) {
        String epicId = "";
        if (Objects.nonNull(subtask.getEpic())) {
            epicId = Integer.toString(subtask.getEpic().getId());
        }
        return String.format("%s,SUBTASK,%s,%s,%s,%s,",
                subtask.getId(),
                subtask.getTitle(),
                subtask.getStatus().toString(),
                subtask.getDescription(),
                epicId);
    }
}