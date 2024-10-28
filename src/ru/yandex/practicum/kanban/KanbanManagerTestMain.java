package ru.yandex.practicum.kanban;

import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
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

        System.out.println("Тесты на equals и hashCode");
        Task object1 = new Task(10, null, null, null);
        Task object2 = new Task(10, null, null, Status.NEW);
        Task object3 = new Task(10, null, " ", Status.IN_PROGRESS);
        if (object1.equals(object2) & object2.equals(object1) & !(object1.equals(null))) {
            if (object3.equals(object2) & object1.equals(object3) & !(object2.equals(null))) {
                if (object1.hashCode() == object2.hashCode() & object3.hashCode() == object1.hashCode()) {
                    System.out.print(" object1.hashCode()=" + object1.hashCode());
                    System.out.print(" object2.hashCode()=" + object2.hashCode());
                    System.out.print(" object2.hashCode()=" + object3.hashCode());
                    System.out.println();
                }
            }
        }
        Subtask st1 = new Subtask(12, null, null, null);
        Subtask st2 = new Subtask(12, null, null, null);
        if (st1.equals(st2) & st2.equals(st1)) {
            if (st1.hashCode() == st2.hashCode()) {
                System.out.print(" st1.hashCode()=" + st1.hashCode());
                System.out.print(" st2.hashCode()=" + st2.hashCode());
            }
        }
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
