package ru.yandex.practicum.kanban.model;

public class TaskDTO {

    private int id;
    private String title;
    private String description;
    private Status status;

    public TaskDTO() {
    }

    public TaskDTO(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public TaskDTO(int id, String title, String description, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
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
}