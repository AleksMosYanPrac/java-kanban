package ru.yandex.practicum.kanban.repository;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TestData {

    public static Task TASK_1 = new Task(1, "task", "description", Status.NEW);
    public static Task TASK_2 = new Task(4, "task", "description", Status.NEW);
    public static Epic EPIC_1 = new Epic(3, "epic", "description", Status.NEW);
    public static Subtask SUBTASK_1 = new Subtask(2, "subtask", "description", Status.NEW);
    public static Subtask SUBTASK_2 = new Subtask(5, "subtask", "description", Status.NEW);

    static List<Task> getListTasks() {
        List<Task> listTasks = new ArrayList<>();
        listTasks.add(TASK_1);
        listTasks.add(SUBTASK_1);
        listTasks.add(EPIC_1);
        listTasks.add(TASK_2);
        listTasks.add(SUBTASK_2);
        return listTasks;
    }
}