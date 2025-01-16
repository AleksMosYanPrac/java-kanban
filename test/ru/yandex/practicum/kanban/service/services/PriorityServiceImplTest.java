package ru.yandex.practicum.kanban.service.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.exceptions.PriorityManagerTimeIntersection;
import ru.yandex.practicum.kanban.service.services.impls.PriorityServiceImpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PriorityServiceImplTest {

    private PriorityService priorityService;

    @BeforeEach
    void init() {
        this.priorityService = new PriorityServiceImpl();
    }

    @Test
    void shouldAddTaskWithStartTimeAndDuration() {
        Task task = new Task(1, "Task", "-", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(3));

        priorityService.add(task);

        assertEquals(1, priorityService.sortByStarTime().size());
        assertTrue(priorityService.sortByStarTime().contains(task));
    }

    @Test
    void shouldNotAddTaskWithoutStartTimeAndDuration() {
        Task task = new Task(1, "Task", "-", Status.NEW);

        priorityService.add(task);

        assertEquals(0, priorityService.sortByStarTime().size());
        assertFalse(priorityService.sortByStarTime().contains(task));
    }

    @Test
    void shouldCheckTimeIntersection() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime nextStartTime = startTime.plusMinutes(60);
        LocalDateTime timeWithIntersection = startTime.plusMinutes(duration.toMinutes()).minusMinutes(1);

        Task task1 = new Task(1, "Task", "-", Status.NEW, startTime, duration);
        Task task2 = new Task(1, "Task", "-", Status.NEW, nextStartTime, duration);
        Task task3 = new Task(1, "Task", "-", Status.NEW, timeWithIntersection, duration);
        priorityService.add(task1);

        assertTrue(priorityService.hasTimeIntersection(task3));
        assertFalse(priorityService.hasTimeIntersection(task2));
    }

    @Test
    void shouldThrowPriorityManagerTimeIntersection() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(30);
        Task task1 = new Task(1, "Task", "-", Status.NEW, startTime, duration);
        Task task2 = new Task(1, "Task", "-", Status.NEW, startTime, duration);
        priorityService.add(task1);


        assertThrows(PriorityManagerTimeIntersection.class, () -> priorityService.add(task2));
    }

    @Test
    void shouldDeleteTask() {
        Task task = new Task(1, "Task", "-", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(3));
        priorityService.add(task);
        Set<Task> sortedSet = priorityService.sortByStarTime();

        priorityService.delete(task);

        assertEquals(1, sortedSet.size());
        assertTrue(sortedSet.contains(task));
        assertEquals(0, priorityService.sortByStarTime().size());
        assertFalse(priorityService.sortByStarTime().contains(task));
    }

    @Test
    void shouldUpdateTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(30);
        Task task = new Task(1, "Task", "-", Status.NEW, startTime, duration);
        Task updatedTask = new Task(1, "Task", "-", Status.IN_PROGRESS, startTime, duration);
        priorityService.add(task);

        priorityService.update(updatedTask);

        assertEquals(1, priorityService.sortByStarTime().size());
        assertEquals(Status.IN_PROGRESS, priorityService.sortByStarTime().stream().findFirst().get().getStatus());
    }
}