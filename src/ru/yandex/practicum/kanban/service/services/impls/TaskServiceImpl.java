package ru.yandex.practicum.kanban.service.services.impls;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskDTO;
import ru.yandex.practicum.kanban.service.services.TaskService;
import ru.yandex.practicum.kanban.service.util.IdGenerator;

public class TaskServiceImpl implements TaskService {

    @Override
    public Task createTask(TaskDTO task) {
        int id = IdGenerator.generate();
        return Task.builder().setId(id).setData(task).buildTask();
    }

    @Override
    public Subtask createSubtask(TaskDTO subtask) {
        int id = IdGenerator.generate();
        return Task.builder().setId(id).setData(subtask).buildSubtask();
    }

    @Override
    public Epic createEpic(TaskDTO epic) {
        int id = IdGenerator.generate();
        return Task.builder().setId(id).setData(epic).buildEpic();
    }

    @Override
    public Epic createEpic(TaskDTO epic, TaskDTO... subtasks) {
        Epic newEpic = createEpic(epic);
        for (TaskDTO subtask : subtasks) {
            Subtask newSubtask = createSubtask(subtask);
            newEpic.addSubtask(newSubtask);
            newSubtask.addEpic(newEpic);
        }
        return newEpic;
    }

    @Override
    public Task createTask(TaskDTO task, Task taskById) {
        return Task.builder().setId(taskById.getId()).setData(task).buildTask();
    }

    @Override
    public Subtask createSubtask(TaskDTO subtask, Subtask subtaskById) {
        Subtask newSubtask = Task.builder().setId(subtaskById.getId()).setData(subtask).buildSubtask();
        newSubtask.addEpic(subtaskById.getEpic());
        return newSubtask;
    }
}