package ru.yandex.practicum.kanban.service.managers;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.service.services.HistoryService;
import ru.yandex.practicum.kanban.service.services.PriorityService;
import ru.yandex.practicum.kanban.service.services.RepositoryService;
import ru.yandex.practicum.kanban.service.services.TaskService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.service.TestDataDTO.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected TaskService taskService;
    protected RepositoryService repositoryService;
    protected HistoryService historyService;
    protected PriorityService priorityService;

    protected abstract void setUp();

    @Test
    protected void canCreateModelObjects() throws Exception {

        Task task = taskManager.createTask(TASK_1);
        Epic epic = taskManager.createEpic(EPIC_1);
        Subtask subtask = taskManager.createSubtask(SUBTASK_1);

        assertAll("Should be added to repositories",
                () -> assertEquals(task, repositoryService.getAllTask().getFirst()),
                () -> assertEquals(epic, repositoryService.getAllEpic().getFirst()),
                () -> assertEquals(subtask, repositoryService.getAllSubtasks().getFirst())
        );
    }

    @Test
    void canCreateComplexEpic() throws Exception {

        Epic epic = taskManager.createEpic(EPIC_1, SUBTASK_1);

        assertAll("Should Add Epic and Subtask to repositories",
                () -> assertEquals(epic, repositoryService.getAllEpic().getFirst()),
                () -> assertEquals(epic.getSubtasks().getFirst(), repositoryService.getAllSubtasks().getFirst())
        );
    }

    @Test
    void shouldFindAndReturnSubtaskListForAvailableEpic() throws Exception {
        Epic epic = taskManager.createEpic(EPIC_1, SUBTASK_1);

        List<Subtask> subtaskList = taskManager.getSubtasksForEpic(epic.getId());

        assertAll("Should return subtaskList which added to Epic and present in repositories",
                () -> assertEquals(epic.getSubtasks(), subtaskList),
                () -> assertEquals(epic.getSubtasks().size(), subtaskList.size())
        );
    }

    @Test
    void shouldReturnEmptySubtaskListWhenAvailableEpicHasNotSubtasks() throws Exception {
        Epic epic = taskManager.createEpic(EPIC_1);

        List<Subtask> subtaskList = taskManager.getSubtasksForEpic(epic.getId());

        assertTrue(subtaskList.isEmpty());
        assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    void shouldThrowNoSuchElementsWhenCanNotReturnSubtaskListForUnavailableEpic() {
        int taskIdForEmptyRepositories = 1;

        assertTrue(repositoryService.getAllEpic().isEmpty());
        assertThrows(NoSuchElementException.class,
                () -> taskManager.getSubtasksForEpic(taskIdForEmptyRepositories)
        );
    }

    @Test
    void canDeleteModelObjects() throws Exception {
        Task task = taskManager.createTask(TASK_1);
        Epic epic = taskManager.createEpic(EPIC_1);
        Subtask subtask = taskManager.createSubtask(SUBTASK_1);

        taskManager.deleteTask(task.getId());
        taskManager.deleteEpic(epic.getId());
        taskManager.deleteSubtask(subtask.getId());

        assertAll("Repositories should be empty after delete",
                () -> assertTrue(repositoryService.getAllTask().isEmpty()),
                () -> assertTrue(repositoryService.getAllEpic().isEmpty()),
                () -> assertTrue(repositoryService.getAllSubtasks().isEmpty())
        );
    }

    @Test
    void shouldUpdateEpicWhenDeleteSubtaskWhichContainsInEpic() throws Exception {
        Epic epic = taskManager.createEpic(EPIC_1, SUBTASK_1);
        Subtask subtask = repositoryService.getAllSubtasks().getFirst();

        taskManager.deleteSubtask(subtask.getId());

        assertAll("Should delete subtask and update Epic",
                () -> assertTrue(repositoryService.getAllSubtasks().isEmpty()),
                () -> assertTrue(repositoryService.getAllEpic().getFirst().getSubtasks().isEmpty())
        );
    }

    @Test
    void shouldDeleteIncludedSubtaskWhenDeleteEpic() throws Exception {
        Epic epic = taskManager.createEpic(EPIC_1, SUBTASK_1);

        taskManager.deleteEpic(epic.getId());

        assertAll("Epic and Subtask repositories should be empty",
                () -> assertTrue(repositoryService.getAllSubtasks().isEmpty()),
                () -> assertTrue(repositoryService.getAllEpic().isEmpty())
        );
    }

    @Test
    void shouldThrowNoSuchElementsWhenDeletedObjectUnavailable() {
        int taskIdForEmptyRepositories = 1;

        assertAll("Repositories should be empty",
                () -> assertTrue(repositoryService.getAllTask().isEmpty()),
                () -> assertTrue(repositoryService.getAllSubtasks().isEmpty()),
                () -> assertTrue(repositoryService.getAllEpic().isEmpty())
        );
        assertThrows(NoSuchElementException.class,
                () -> taskManager.deleteTask(taskIdForEmptyRepositories));
        assertThrows(NoSuchElementException.class,
                () -> taskManager.deleteSubtask(taskIdForEmptyRepositories));
        assertThrows(NoSuchElementException.class,
                () -> taskManager.deleteEpic(taskIdForEmptyRepositories));
    }

    @Test
    void canUpdateDataForTask() throws Exception {
        Task task = taskManager.createTask(new TaskDTO("TASK", "TASK FOR TEST", "NEW"));

        taskManager.updateTask(new TaskDTO(task.getId(), "TASK", "TASK FOR TEST", "IN_PROGRESS"));
        Task updatedTask = repositoryService.getAllTask().getFirst();

        assertEquals(task.getId(), updatedTask.getId());
        assertNotEquals(task.getStatus(), updatedTask.getStatus());
    }

    @Test
    void canUpdateDataForSubtaskWithoutEpic() throws Exception {
        Subtask subtask = taskManager.createSubtask(new TaskDTO("TASK", "TASK FOR TEST", "NEW"));

        taskManager.updateSubtask(new TaskDTO(subtask.getId(), "TASK", "-", "IN_PROGRESS"));
        Subtask updatedSubtask = repositoryService.getAllSubtasks().getFirst();

        assertEquals(subtask.getId(), updatedSubtask.getId());
        assertNotEquals(subtask.getStatus(), updatedSubtask.getStatus());
    }

    @Test
    void shouldUpdateEpicDataWhenUpdateSubtask() throws Exception {
        Epic epic = taskManager.createEpic(EPIC_1, SUBTASK_1);
        Subtask subtask = epic.getSubtasks().getFirst();

        taskManager.updateSubtask(new TaskDTO(subtask.getId(), "SUBTASK", "-", "IN_PROGRESS"));
        Subtask updatedSubtask = repositoryService.getAllSubtasks().getFirst();
        Epic updatedEpic = repositoryService.getAllEpic().getFirst();

        assertAll("",
                () -> assertEquals(epic, updatedEpic),
                () -> assertEquals(subtask, updatedSubtask),
                () -> assertEquals(Status.IN_PROGRESS, updatedSubtask.getStatus()),
                () -> assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus())
        );
    }

    @Test
    void shouldFindModelObjectsByIntegerId() throws Exception {
        Task task = taskManager.createTask(TASK_1);
        Epic epic = taskManager.createEpic(EPIC_1);
        Subtask subtask = taskManager.createSubtask(SUBTASK_1);

        assertAll("Should be equals",
                () -> assertEquals(task, taskManager.getTaskById(task.getId())),
                () -> assertEquals(epic, taskManager.getEpicById(epic.getId())),
                () -> assertEquals(subtask, taskManager.getSubTaskById(subtask.getId()))
        );
    }

    @Test
    void shouldThrowNoSuchElementsWhenCanNotFindModelObjects() {
        int taskIdForEmptyRepositories = 1;

        assertAll("Repositories should be empty",
                () -> assertTrue(repositoryService.getAllTask().isEmpty()),
                () -> assertTrue(repositoryService.getAllSubtasks().isEmpty()),
                () -> assertTrue(repositoryService.getAllEpic().isEmpty())
        );
        assertThrows(NoSuchElementException.class, () -> taskManager.getTaskById(taskIdForEmptyRepositories));
        assertThrows(NoSuchElementException.class, () -> taskManager.getSubTaskById(taskIdForEmptyRepositories));
        assertThrows(NoSuchElementException.class, () -> taskManager.getEpicById(taskIdForEmptyRepositories));
    }
}