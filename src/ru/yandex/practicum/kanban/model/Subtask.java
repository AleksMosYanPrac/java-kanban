package ru.yandex.practicum.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {

    private Epic epic;

    public Subtask(int id, String title, String description, Status status) {
        super(id, title, description, status);
    }

    public Subtask(
            int id,
            String title,
            String description,
            Status status,
            LocalDateTime startTime,
            Duration duration) {
        super(id, title, description, status, startTime, duration);
    }

    public void addEpic(Epic epic) {
        this.epic = epic;
    }

    public Epic getEpic() {
        return this.epic;
    }

    public boolean hasEpic() {
        return Objects.nonNull(epic);
    }

    @Override
    public String toString() {
        String epicId = "null";
        if (Objects.nonNull(epic)) {
            epicId = String.valueOf(epic.getId());
        }
        if (Objects.nonNull(startTime) && Objects.nonNull(duration)) {
            String dateTime = startTime.format(Task.DATE_TIME_FORMATTER);
            long durationInMinutes = duration.toMinutes();
            return "Subtask{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", status=" + status +
                    ", epicId=" + epicId +
                    ", startTime=" + dateTime +
                    ", duration[minutes]=" + durationInMinutes +
                    '}';
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