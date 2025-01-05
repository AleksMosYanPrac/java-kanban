package ru.yandex.practicum.kanban.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

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

    @Test
    void shouldCalculateEndTime(){
        LocalDateTime startTime = LocalDateTime.of(2025, Month.JANUARY,1,0,0);
        long minutes = 60;
        Duration duration = Duration.ofMinutes(minutes);
        Task task = new Task(1,"task 1","task with time",Status.NEW,startTime,duration);

        LocalDateTime endTime = task.getEndTime();

        Assertions.assertEquals(startTime.plusMinutes(minutes),endTime);
    }
}