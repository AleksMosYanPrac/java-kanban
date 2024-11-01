package ru.yandex.practicum.kanban.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.repository.Repository;
import ru.yandex.practicum.kanban.repository.impls.InMemoryEpicRepositoryImpl;
import ru.yandex.practicum.kanban.repository.impls.InMemoryHistoryRepository;
import ru.yandex.practicum.kanban.repository.impls.InMemorySubtaskRepositoryImpl;
import ru.yandex.practicum.kanban.repository.impls.InMemoryTaskRepositoryImpl;

import java.util.List;

class TaskManagerTest {

    private Repository<Task> taskRepository = new InMemoryTaskRepositoryImpl();
    private Repository<Epic> epicRepository = new InMemoryEpicRepositoryImpl();
    private Repository<Subtask> subtaskRepository = new InMemorySubtaskRepositoryImpl();
    private HistoryManager historyManager = new HistoryManagerImpl(new InMemoryHistoryRepository());
    private TaskManager taskManager = new TaskManagerImpl(taskRepository, epicRepository, subtaskRepository, historyManager);

    @BeforeEach
    void clearTasksRepositories() {
        taskRepository.deleteAll();
        epicRepository.deleteAll();
        subtaskRepository.deleteAll();
    }

    @Test
    void canAddDifferentTypeTask() {
        TaskDTO task = new TaskDTO("Task", "Description", Status.NEW);
        TaskDTO epic = new TaskDTO("Epic", "description", Status.NEW);
        TaskDTO subtask = new TaskDTO("Subtask", "description", Status.NEW);

        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        Assertions.assertEquals(1, taskRepository.getAll().size());
        Assertions.assertEquals(1, epicRepository.getAll().size());
        Assertions.assertEquals(1, subtaskRepository.getAll().size());
    }

    @Test
    void canSafePreviouslyVersionOfTaskInHistoryManager() {
        TaskDTO task = new TaskDTO("Task", "Description", Status.NEW);
        TaskDTO updatedTask = new TaskDTO("Updated", "Updated", Status.IN_PROGRESS);
        int id = taskManager.createTask(task).getId();
        taskManager.getTaskById(id);
        updatedTask.setId(id);
        taskManager.updateTask(updatedTask);
        taskManager.getTaskById(id);

        List<Task> viewedTasks = taskManager.getHistoryOfViewedTasks();

        Assertions.assertEquals(2, viewedTasks.size());
        Assertions.assertEquals(task.getTitle(), viewedTasks.getFirst().getTitle());
        Assertions.assertEquals(updatedTask.getTitle(), viewedTasks.get(1).getTitle());
    }

    @Test
    void shouldAddToHistory() {
        TaskDTO epic = new TaskDTO("Epic", "Description", Status.NEW);
        int id = taskManager.createEpic(epic).getId();

        taskManager.getEpicById(id);
        List<Task> viewedTasks = historyManager.getHistory();

        Assertions.assertEquals(1, viewedTasks.size());
        Assertions.assertEquals(epic.getTitle(), viewedTasks.getFirst().getTitle());
    }

    @Test
    void shouldCreateEpicWithLinkedSubtask() {
        TaskDTO epic = new TaskDTO("Epic", "description", Status.NEW);
        TaskDTO subtask = new TaskDTO("Subtask", "description", Status.NEW);

        taskManager.createEpic(epic, subtask);
        Epic addedEpic = epicRepository.getAll().getFirst();
        Subtask addedSubtask = subtaskRepository.getAll().getFirst();

        Assertions.assertEquals(addedEpic, addedSubtask.getEpic());
        Assertions.assertEquals(addedSubtask, addedEpic.getSubtasks().getFirst());
    }

    @Test
    void shouldDeleteSubtaskWhenDeleteEpic() {
        TaskDTO epic = new TaskDTO("Epic", "description", Status.NEW);
        TaskDTO subtask = new TaskDTO("Subtask", "description", Status.NEW);
        int id = taskManager.createEpic(epic, subtask).getId();
        Epic addedEpic = epicRepository.getAll().getFirst();
        Subtask addedSubtask = subtaskRepository.getAll().getFirst();
        epic.setId(id);

        taskManager.deleteEpic(epic);

        Assertions.assertTrue(epicRepository.getAll().isEmpty());
        Assertions.assertTrue(subtaskRepository.getAll().isEmpty());
    }

    @Test
    void shouldUnlinkDeletedSubtaskAndUpdateEpic() {
        TaskDTO epic = new TaskDTO("Epic", "description", Status.NEW);
        TaskDTO subtask = new TaskDTO("Subtask", "description", Status.NEW);
        taskManager.createEpic(epic, subtask);

        Subtask addedSubtask = subtaskRepository.getAll().getFirst();
        subtask.setId(addedSubtask.getId());

        taskManager.deleteSubtask(subtask);
        Epic updatedEpic = epicRepository.getAll().getFirst();

        Assertions.assertTrue(subtaskRepository.getAll().isEmpty());
        Assertions.assertTrue(updatedEpic.getSubtasks().isEmpty());
    }
}