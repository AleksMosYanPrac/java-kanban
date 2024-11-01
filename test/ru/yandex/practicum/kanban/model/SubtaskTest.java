package ru.yandex.practicum.kanban.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SubtaskTest {

    @Test
    void shouldEqualsWithSameId() {
        Subtask subtask1 = new Subtask(1, "Subtask1", "description", Status.NEW);
        Subtask subtask2 = new Subtask(1, "subtask2", "description", Status.NEW);

        Assertions.assertEquals(subtask1, subtask2);
        Assertions.assertEquals(subtask2, subtask1);
    }

    @Test
    void shouldHaveSameHashcodeWhenEquals() {
        Subtask subtask1 = new Subtask(1, "subtask1", "description", Status.NEW);
        Subtask subtask2 = new Subtask(1, "subtask2", "description", Status.NEW);

        Assertions.assertEquals(subtask1.hashCode(), subtask2.hashCode());
        Assertions.assertEquals(subtask2.hashCode(), subtask1.hashCode());
    }

    @Test
    void shouldNotEqualsToNull() {
        Subtask subtask1 = new Subtask(1, "subtask1", "description", Status.NEW);
        Subtask subtask2 = null;

        Assertions.assertNotEquals(subtask1, subtask2);
        Assertions.assertNotEquals(subtask2, subtask1);
    }
}