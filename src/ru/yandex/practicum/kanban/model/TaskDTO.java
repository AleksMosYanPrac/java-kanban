package ru.yandex.practicum.kanban.model;

public class TaskDTO {

    private int id;
    private String title;
    private String description;
    private Status status;
    private String startTime;
    private long durationInMinutes;

    public TaskDTO() {
    }

    public TaskDTO(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public TaskDTO(String title, String description, Status status, String startTime, long durationInMinutes) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.durationInMinutes = durationInMinutes;
    }

    public TaskDTO(int id, String title, String description, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public TaskDTO(int id, String title, String description, Status status, String startTime, long durationInMinutes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.durationInMinutes = durationInMinutes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public long getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(long durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }
}