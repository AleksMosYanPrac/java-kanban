package ru.yandex.practicum.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Epic extends Task {

    private List<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
        this.subtasks = new ArrayList<>();
    }

    public Epic(int id, String title, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(id, title, description, status, startTime, duration);
        this.subtasks = new ArrayList<>();
        this.endTime = startTime.plusMinutes(duration.toMinutes());
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void addSubtask(Subtask subtask) {
        this.subtasks.add(subtask);
        checkAndUpdateEpicStatus();
        checkAndUpdateEpicTime();
    }

    public void deleteSubtask(Subtask subtask) {
        this.subtasks.remove(subtask);
        checkAndUpdateEpicStatus();
        checkAndUpdateEpicTime();
    }

    public List<Subtask> getSubtasks() {
        return this.subtasks;
    }

    private void checkAndUpdateEpicStatus() {
        Set<Status> subtaskStatuses = subtasks.stream().map(Subtask::getStatus).collect(Collectors.toSet());
        if (subtaskStatuses.size() > 1) {
            super.status = Status.IN_PROGRESS;
        } else {
            super.status = subtaskStatuses.stream().findFirst().orElse(Status.NEW);
        }
    }

    private void checkAndUpdateEpicTime() {
        Optional<Subtask> subtaskWithEarliestStartTime =
                subtasks.stream()
                        .filter(subtask -> Objects.nonNull(subtask.getStartTime()))
                        .min(Comparator.comparing(subtask -> subtask.startTime));
        Optional<Subtask> subtaskWithLatestStartTime =
                subtasks.stream()
                        .filter(subtask -> Objects.nonNull(subtask.getStartTime()))
                        .max(Comparator.comparing(subtask -> subtask.startTime));

        if (subtaskWithEarliestStartTime.isPresent() & subtaskWithLatestStartTime.isPresent()) {
            super.startTime = subtaskWithEarliestStartTime.get().getStartTime();
            this.endTime = subtaskWithLatestStartTime.get().getEndTime();
            super.duration = Duration.between(startTime, endTime);
        }
    }

    @Override
    public String toString() {
        String listSubtaskId = getListSubtaskId();
        return "Epic{" +
                "id=" + super.getId() +
                ", title='" + super.getTitle() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", subtasksId=" + listSubtaskId +
                '}';
    }

    private String getListSubtaskId() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < subtasks.size(); i++) {
            sb.append(subtasks.get(i).getId());
            if (i < (subtasks.size() - 1)) {
                sb.append(",");
            }
        }
        sb.append("]");

        return sb.toString();
    }
}