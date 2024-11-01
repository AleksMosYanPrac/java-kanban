package ru.yandex.practicum.kanban.model;

import java.util.Objects;

public class Subtask extends Task {

    private Epic epic;

    public Subtask(int id, String title, String description, Status status) {
        super(id, title, description, status);
    }

    public void addEpic(Epic epic) {
        this.epic = epic;
    }

    public Epic getEpic() {
        return this.epic;
    }

    @Override
    public String toString() {
        String epicId = "null";
        if (Objects.nonNull(epic)) {
            epicId = String.valueOf(epic.getId());
        }
        return "Subtask{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epicId=" + epicId +
                '}';
    }
}