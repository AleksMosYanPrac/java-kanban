package ru.yandex.practicum.kanban.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Epic extends Task {

    private List<Subtask> subtasks;

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        this.subtasks.add(subtask);
        checkAndUpdateEpicStatus();
    }

    public void deleteSubtask(Subtask subtask) {
        this.subtasks.remove(subtask);
        checkAndUpdateEpicStatus();
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