package ru.yandex.practicum.kanban.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    void shouldEqualsWithSameId() {
        Task task1 = new Task(1, "task1", "description", Status.NEW);
        Task task2 = new Task(1, "task2", "description", Status.NEW);

        Assertions.assertEquals(task1, task2);
        Assertions.assertEquals(task2, task1);
    }

    @Test
    void shouldHaveSameHashcodeWhenEquals() {
        Task task1 = new Task(1, "task1", "description", Status.NEW);
        Task task2 = new Task(1, "task2", "description", Status.NEW);

        Assertions.assertEquals(task1.hashCode(), task2.hashCode());
        Assertions.assertEquals(task2.hashCode(), task1.hashCode());
    }

    @Test
    void shouldNotEqualsToNull() {
        Task task1 = new Task(1, "task1", "description", Status.NEW);
        Task task2 = null;

        Assertions.assertNotEquals(task1, task2);
        Assertions.assertNotEquals(task2, task1);
    }
}