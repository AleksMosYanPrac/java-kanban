package ru.yandex.practicum.kanban.service.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public interface PriorityManagerTest {

    @Test
    void shouldReturnSortedByStartTimeTaskSet();

    @Test
    void shouldThrowStartTimeExceptionWhenAddTaskWithTimeIntersection();

    @Test
    void shouldThrowStartTimeExceptionWhenUpdatedTaskHasTimeIntersection();

}