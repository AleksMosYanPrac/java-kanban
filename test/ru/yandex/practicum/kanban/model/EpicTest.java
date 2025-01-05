package ru.yandex.practicum.kanban.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

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

    @Test
    void shouldCalculateEndTimeEpicWithoutSubtasks() {
        LocalDateTime startTime = LocalDateTime.of(2025, Month.JANUARY, 1, 0, 0);
        long minutes = 60;
        Duration duration = Duration.ofMinutes(minutes);
        Epic epic = new Epic(1, "epic 1", "epic with time", Status.NEW, startTime, duration);

        LocalDateTime endTime = epic.getEndTime();

        Assertions.assertEquals(startTime.plusMinutes(minutes), endTime);
    }
    @Test
    void shouldNotUpdateEpicTimeWhenAddOneSubtasksWithoutTime() {
        LocalDateTime startTime = LocalDateTime.of(2025, Month.JANUARY, 1, 0, 0);
        long minutes = 60;
        Duration duration = Duration.ofMinutes(minutes);
        Subtask subtask = new Subtask(2,"Subtask","Subtask without time",Status.NEW);
        Epic epic = new Epic(1, "epic 1", "epic with time", Status.NEW, startTime, duration);
        epic.addSubtask(subtask);

        LocalDateTime epicStartTime = epic.getStartTime();
        LocalDateTime epicEndTime = epic.getEndTime();
        Duration epicDuration = epic.getDuration();

        Assertions.assertEquals(startTime,epicStartTime);
        Assertions.assertEquals(startTime.plusMinutes(minutes), epicEndTime);
        Assertions.assertEquals(duration,epicDuration);
    }

    @Test
    void shouldUpdateEpicTimeWhenAddOneSubtasksWithTime() {
        LocalDateTime startTime = LocalDateTime.of(2025, Month.JANUARY, 1, 0, 0);
        long minutes = 60;
        Duration duration = Duration.ofMinutes(minutes);
        Subtask subtask = new Subtask(2,"Subtask","Subtask with time",Status.NEW,startTime,duration);
        Epic epic = new Epic(1, "epic 1", "epic without time", Status.NEW);
        epic.addSubtask(subtask);

        LocalDateTime epicStartTime = epic.getStartTime();
        LocalDateTime epicEndTime = epic.getEndTime();
        Duration epicDuration = epic.getDuration();

        Assertions.assertEquals(startTime,epicStartTime);
        Assertions.assertEquals(startTime.plusMinutes(minutes), epicEndTime);
        Assertions.assertEquals(duration,epicDuration);
    }

    @Test
    void shouldUpdateEpicTimeWhenAddTwoSubtasksWithDifferentTime() {
        LocalDateTime startTime_1 = LocalDateTime.of(2025, Month.JANUARY, 1, 0, 0);
        LocalDateTime startTime_2 = LocalDateTime.of(2025, Month.FEBRUARY, 1, 0, 0);
        long minutes = 60;
        Duration duration = Duration.ofMinutes(minutes);
        Epic epic = new Epic(1, "epic 1", "epic without time", Status.NEW);
        Subtask subtask_1 = new Subtask(2,"Subtask","With time",Status.NEW,startTime_1,duration);
        Subtask subtask_2 = new Subtask(3,"Subtask","With time",Status.NEW,startTime_2,duration);
        epic.addSubtask(subtask_1);
        epic.addSubtask(subtask_2);

        LocalDateTime epicStartTime = epic.getStartTime();
        LocalDateTime epicEndTime = epic.getEndTime();
        Duration epicDuration = epic.getDuration();
        Duration durationBetweenSubtasks = Duration.between(subtask_1.getStartTime(),subtask_2.getEndTime());

        Assertions.assertEquals(subtask_1.getStartTime(),epicStartTime);
        Assertions.assertEquals(subtask_2.getEndTime(), epicEndTime);
        Assertions.assertEquals(durationBetweenSubtasks,epicDuration);
    }
}