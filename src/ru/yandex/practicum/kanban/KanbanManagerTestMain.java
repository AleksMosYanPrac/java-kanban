package ru.yandex.practicum.kanban;

import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.TaskDTO;
import ru.yandex.practicum.kanban.repository.EpicRepository;
import ru.yandex.practicum.kanban.repository.SubtaskRepository;
import ru.yandex.practicum.kanban.repository.TaskRepository;
import ru.yandex.practicum.kanban.service.TaskManager;

public class KanbanManagerTestMain {

    public static void main(String[] args) {

        TaskRepository taskRepository = new TaskRepository();
        SubtaskRepository subtaskRepository = new SubtaskRepository();
        EpicRepository epicRepository = new EpicRepository();
        TaskManager taskManager = new TaskManager(taskRepository, epicRepository, subtaskRepository);

        TaskDTO task1 = new TaskDTO("Task 1", "Some task 1", Status.NEW);
        TaskDTO task2 = new TaskDTO("Task 2", "Some task 2", Status.NEW);

        TaskDTO epic1 = new TaskDTO("Epic 1", "Some epic 1", Status.NEW);
        TaskDTO subtask1 = new TaskDTO("SubTask 1", "Some subTask 1", Status.NEW);
        TaskDTO subtask2 = new TaskDTO("SubTask 2", "Some subTask 2", Status.NEW);

        TaskDTO epic2 = new TaskDTO("Epic 2", "Some epic 2", Status.NEW);
        TaskDTO subtask3 = new TaskDTO("SubTask 3", "Some subTask 3", Status.NEW);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1, subtask1, subtask2);
        taskManager.createEpic(epic2, subtask3);

        System.out.println("Списки созданных задач:");
        printAll(taskRepository, epicRepository, subtaskRepository);

        task1 = new TaskDTO(1, "Task 1", "Some task 1", Status.IN_PROGRESS);
        task2 = new TaskDTO(2, "Task 2", "Some task 2", Status.DONE);
        taskManager.updateTask(task1);
        taskManager.updateTask(task2);

        subtask1 = new TaskDTO(4, "SubTask 1", "Some subTask 1", Status.DONE);
        subtask2 = new TaskDTO(5, "SubTask 2", "Some subTask 2", Status.NEW);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        subtask3 = new TaskDTO(7, "SubTask 3", "Some subTask 3", Status.DONE);
        taskManager.updateSubtask(subtask3);

        System.out.println("Списки обновленных задач:");
        printAll(taskRepository, epicRepository, subtaskRepository);

        task1.setId(1);
        epic2.setId(6);
        taskManager.deleteTask(task1);
        taskManager.deleteEpic(epic2);

        System.out.println("Задачи задач после удаления:");
        printAll(taskRepository, epicRepository, subtaskRepository);

        System.out.println("Задачи задач после удаления одной подзадачи:");
        printAll(taskRepository, epicRepository, subtaskRepository);

        System.out.println("Статус Эпика должен вернуться к New после удаления всех его субтасков");
        taskManager.deleteSubtask(subtask2);
        taskManager.deleteSubtask(subtask1);
        printAll(taskRepository, epicRepository, subtaskRepository);
    }

    private static void printAll(TaskRepository taskRepository,
                                 EpicRepository epicRepository,
                                 SubtaskRepository subtaskRepository) {
        System.out.println(taskRepository.getAll());
        System.out.println(subtaskRepository.getAll());
        System.out.println(epicRepository.getAll());
        System.out.println();
    }
}
