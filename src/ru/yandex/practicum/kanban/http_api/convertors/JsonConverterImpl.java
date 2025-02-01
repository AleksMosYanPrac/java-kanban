package ru.yandex.practicum.kanban.http_api.convertors;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.kanban.http_api.RequestConverter;
import ru.yandex.practicum.kanban.http_api.ResponseConverter;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskDTO;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class JsonConverterImpl implements ResponseConverter, RequestConverter {

    private final Gson gson;

    public JsonConverterImpl(boolean prettyPrinting) {
        this.gson = GSONBuilder.get(prettyPrinting);
    }

    @Override
    public String convert(Task task) {
        return gson.toJson(task);
    }

    @Override
    public String convert(Subtask subtask) {
        return gson.toJson(subtask);
    }

    @Override
    public String convert(Epic epic) {
        return gson.toJson(epic);
    }

    @Override
    public String convert(Collection<? extends Task> collection) {
        return gson.toJson(collection);
    }

    @Override
    public TaskDTO convertToObject(String json) {
        return gson.fromJson(json, TaskDTO.class);
    }

    @Override
    public List<TaskDTO> convertToList(String jsonArray) {
        JsonElement jsonElement = JsonParser.parseString(jsonArray);
        if (jsonElement.isJsonArray()) {
            return gson.fromJson(jsonArray, new ListTaskDTOTypeToken().getType());
        } else {
            return List.of(convertToObject(jsonArray));
        }
    }

    private static class ListTaskDTOTypeToken extends TypeToken<List<TaskDTO>> {
    }

    private static class GSONBuilder {

        public static Gson get(boolean prettyPrinting) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.serializeNulls();
            gsonBuilder.registerTypeAdapter(Task.class, new GSONBuilder.TaskTypeAdapter());
            gsonBuilder.registerTypeAdapter(Subtask.class, new GSONBuilder.SubtaskTypeAdapter());
            gsonBuilder.registerTypeAdapter(Epic.class, new GSONBuilder.EpicTypeAdapter());
            if (prettyPrinting) {
                gsonBuilder.setPrettyPrinting();
            }
            return gsonBuilder.create();
        }

        static class TaskTypeAdapter extends TypeAdapter<Task> {

            @Override
            public void write(JsonWriter writer, Task task) throws IOException {
                writer.beginObject();
                writer.name("type").value("task");
                writer.name("id").value(task.getId());
                writer.name("title").value(task.getTitle());
                writer.name("description").value(task.getDescription());
                writer.name("status").value(task.getStatus().toString());
                if (task.hasStartTimeAndDuration()) {
                    writer.name("startTime").value(task.getStartTime().format(Task.DATE_TIME_FORMATTER));
                    writer.name("endTime").value(task.getEndTime().format(Task.DATE_TIME_FORMATTER));
                    writer.name("durationInMinutes").value(task.getDuration().toMinutes());
                }
                writer.endObject();
            }

            @Override
            public Task read(JsonReader reader) throws IOException {
                return null;
            }
        }

        static class SubtaskTypeAdapter extends TypeAdapter<Subtask> {

            @Override
            public void write(JsonWriter writer, Subtask subtask) throws IOException {
                writer.setSerializeNulls(true);
                writer.beginObject();
                writer.name("type").value("subtask");
                writer.name("id").value(subtask.getId());
                writer.name("title").value(subtask.getTitle());
                writer.name("description").value(subtask.getDescription());
                writer.name("status").value(subtask.getStatus().toString());
                if (subtask.hasStartTimeAndDuration()) {
                    writer.name("startTime").value(subtask.getStartTime().format(Task.DATE_TIME_FORMATTER));
                    writer.name("endTime").value(subtask.getEndTime().format(Task.DATE_TIME_FORMATTER));
                    writer.name("durationInMinutes").value(subtask.getDuration().toMinutes());
                }
                if (Objects.isNull(subtask.getEpic())) {
                    writer.name("epic_id").nullValue();
                } else {
                    writer.name("epic_id").value(subtask.getEpic().getId());
                }
                writer.endObject();
            }

            @Override
            public Subtask read(JsonReader reader) throws IOException {
                return null;
            }
        }

        static class EpicTypeAdapter extends TypeAdapter<Epic> {

            @Override
            public void write(JsonWriter writer, Epic epic) throws IOException {
                writer.setSerializeNulls(true);
                writer.beginObject();
                writer.name("type").value("epic");
                writer.name("id").value(epic.getId());
                writer.name("title").value(epic.getTitle());
                writer.name("description").value(epic.getDescription());
                writer.name("status").value(epic.getStatus().toString());
                if (epic.hasStartTimeAndDuration()) {
                    writer.name("startTime").value(epic.getStartTime().format(Task.DATE_TIME_FORMATTER));
                    writer.name("endTime").value(epic.getEndTime().format(Task.DATE_TIME_FORMATTER));
                    writer.name("durationInMinutes").value(epic.getDuration().toMinutes());
                }
                if (epic.getSubtasks().isEmpty()) {
                    writer.name("subtasksId").beginArray().endArray();
                } else {
                    writer.name("subtasksId");
                    writeAsIntArray(writer, epic.getSubtasks().stream().map(Task::getId).toList());
                }
                writer.endObject();
            }

            private void writeAsIntArray(JsonWriter writer, List<Integer> list) throws IOException {
                writer.beginArray();
                for (Integer i : list) {
                    writer.value(i);
                }
                writer.endArray();
            }

            @Override
            public Epic read(JsonReader reader) throws IOException {
                return null;
            }
        }
    }
}