package ru.yandex.practicum.kanban.repository.impls.in_file.datasource;

public class DataQuery {

    private String key;
    private String value;

    public DataQuery(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key.toLowerCase();
    }

    public String getValue() {
        return value;
    }
}