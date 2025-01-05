package ru.yandex.practicum.kanban.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

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

    @Test
    void shouldCalculateEndTime(){
        LocalDateTime startTime = LocalDateTime.of(2025, Month.JANUARY,1,0,0);
        long minutes = 60;
        Duration duration = Duration.ofMinutes(minutes);
        Subtask subtask = new Subtask(1,"sub 1","subtask with time",Status.NEW,startTime,duration);

        LocalDateTime endTime = subtask.getEndTime();

        Assertions.assertEquals(startTime.plusMinutes(minutes),endTime);
    }
}