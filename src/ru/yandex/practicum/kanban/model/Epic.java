package ru.yandex.practicum.kanban.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        if (subtasks.isEmpty()) {
            super.status = Status.NEW;
        } else {
            Set<Status> subtaskStatuses = new HashSet<>();
            for (Subtask subtask : subtasks) {
                subtaskStatuses.add(subtask.getStatus());
            }
            if (subtaskStatuses.size() > 1) {
                super.status = Status.IN_PROGRESS;
            } else {
                super.status = subtaskStatuses.iterator().next();
            }
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
