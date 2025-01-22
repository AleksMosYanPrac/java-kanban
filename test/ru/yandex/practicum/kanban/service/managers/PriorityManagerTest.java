package ru.yandex.practicum.kanban.service.managers;

import org.junit.jupiter.api.Test;

public interface PriorityManagerTest {

    @Test
    void shouldReturnSortedByStartTimeTaskSet();

    @Test
    void shouldThrowStartTimeExceptionWhenAddTaskWithTimeIntersection();

    @Test
    void shouldThrowStartTimeExceptionWhenUpdatedTaskHasTimeIntersection();

}