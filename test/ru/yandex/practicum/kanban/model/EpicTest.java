package ru.yandex.practicum.kanban.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EpicTest {

    @Test
    void shouldHaveDoneStatusWhenSubtasksHaveDoneStatus() {
        Epic epic = new Epic(1, "epic", "description", Status.NEW);
        Subtask subtask1 = new Subtask(2, "subtask", "description", Status.DONE);
        Subtask subtask2 = new Subtask(2, "subtask", "description", Status.DONE);

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        Assertions.assertSame(epic.getStatus(), Status.DONE);
    }

    @Test
    void shouldHaveInProgressStatusWhenSubtasksHaveDifferentStatus() {
        Epic epic = new Epic(1, "epic", "description", Status.NEW);
        Subtask subtask1 = new Subtask(2, "subtask", "description", Status.NEW);
        Subtask subtask2 = new Subtask(2, "subtask", "description", Status.DONE);

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        Assertions.assertSame(epic.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    void shouldUpdateStatusWhenSubtasksAdd() {
        Epic epic = new Epic(1, "epic", "description", Status.NEW);
        Subtask subtask = new Subtask(2, "subtask", "description", Status.IN_PROGRESS);

        epic.addSubtask(subtask);

        Assertions.assertSame(epic.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    void shouldUpdateStatusWhenSubtasksDelete() {
        Epic epic = new Epic(1, "epic", "description", Status.NEW);
        Subtask subtask = new Subtask(2, "subtask", "description", Status.IN_PROGRESS);

        epic.addSubtask(subtask);
        epic.deleteSubtask(subtask);

        Assertions.assertSame(epic.getStatus(), Status.NEW);
    }

    @Test
    void shouldEqualsWithSameId() {
        Epic epic1 = new Epic(1, "Epic1", "description", Status.NEW);
        Epic epic2 = new Epic(1, "Epic2", "description", Status.NEW);

        Assertions.assertEquals(epic1, epic2);
        Assertions.assertEquals(epic2, epic1);
    }

    @Test
    void shouldHaveSameHashcodeWhenEquals() {
        Epic epic1 = new Epic(1, "epic1", "description", Status.NEW);
        Epic epic2 = new Epic(1, "epic2", "description", Status.NEW);

        Assertions.assertEquals(epic1.hashCode(), epic2.hashCode());
        Assertions.assertEquals(epic2.hashCode(), epic1.hashCode());
    }

    @Test
    void shouldNotEqualsToNull() {
        Epic epic1 = new Epic(1, "epic1", "description", Status.NEW);
        Epic epic2 = null;

        Assertions.assertNotEquals(epic1, epic2);
        Assertions.assertNotEquals(epic2, epic1);
    }
}