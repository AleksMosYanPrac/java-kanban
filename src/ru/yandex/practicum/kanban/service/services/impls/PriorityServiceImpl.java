package ru.yandex.practicum.kanban.service.services.impls;

import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.exceptions.PriorityManagerTimeIntersection;
import ru.yandex.practicum.kanban.service.services.PriorityService;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class PriorityServiceImpl implements PriorityService {

    private final TreeSet<Task> prioritizedTaskSet;

    public PriorityServiceImpl() {
        this.prioritizedTaskSet = new TreeSet<>(new StartTimeComparator());
    }

    public PriorityServiceImpl(List<Task> taskList) {
        this.prioritizedTaskSet = new TreeSet<>(new StartTimeComparator());
        taskList.forEach(this::add);
    }

    @Override
    public Task add(Task task) {
        if (task.hasStartTimeAndDuration()) {
            if (hasTimeIntersection(task)) {
                throw new PriorityManagerTimeIntersection(
                        "Task with id: " + task.getId() + " has time intersection with another Tasks");
            } else {
                prioritizedTaskSet.add(task);
            }
        }
        return task;
    }

    @Override
    public boolean hasTimeIntersection(Task task) {
        return prioritizedTaskSet.stream().anyMatch(t ->
                !((t.getStartTime().isBefore(task.getStartTime()) && t.getEndTime().isBefore(task.getStartTime())) ||
                        (t.getStartTime().isAfter(task.getEndTime()) && t.getEndTime().isAfter(task.getEndTime())))
        );
    }

    @Override
    public TreeSet<Task> sortByStarTime() {
        return new TreeSet<>(prioritizedTaskSet);
    }

    @Override
    public void update(Task updatedTask) {
        delete(updatedTask);
        add(updatedTask);
    }

    @Override
    public void delete(Task deletedTask) {
        prioritizedTaskSet.remove(deletedTask);
    }

    private static class StartTimeComparator implements Comparator<Task> {

        @Override
        public int compare(Task t1, Task t2) {
            return t1.getStartTime().compareTo(t2.getStartTime());
        }
    }
}