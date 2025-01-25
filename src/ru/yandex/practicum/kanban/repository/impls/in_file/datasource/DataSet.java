package ru.yandex.practicum.kanban.repository.impls.in_file.datasource;

import java.util.HashMap;
import java.util.Map;

public class DataSet {

    private final Map<String, String> data;

    public DataSet(Map<String, String> data) {
        this.data = data;
    }

    public int getInt(String label) {
        return Integer.parseInt(getString(label));
    }

    public String getString(String label) {
        return data.getOrDefault(label, "");
    }

    public static DataSetBuilder builder() {
        return new DataSetBuilder();
    }

    public long getLong(String duration) {
        if (this.data.getOrDefault(duration, "0").isBlank()) {
            return 0;
        }
        return Long.parseLong(this.data.getOrDefault(duration, "0"));
    }

    public static class DataSetBuilder {

        private Map<String, String> data;

        public DataSetBuilder() {
            this.data = new HashMap<>();
        }

        public DataSetBuilder add(String label, String data) {
            this.data.put(label, data);
            return this;
        }

        public DataSetBuilder add(String label, int data) {
            this.data.put(label, String.valueOf(data));
            return this;
        }

        public DataSet build() {
            return new DataSet(data);
        }
    }
}