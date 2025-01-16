package ru.yandex.practicum.kanban.model;

public class TaskDTO {

    private int id;
    private String title;
    private String description;
    private String status;
    private String startTime;
    private long durationInMinutes;

    public TaskDTO() {
    }

    public TaskDTO(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public TaskDTO(String title, String description, String status, String startTime, long durationInMinutes) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.durationInMinutes = durationInMinutes;
    }

    public TaskDTO(int id, String title, String description, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public TaskDTO(int id, String title, String description, String status, String startTime, long durationInMinutes) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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