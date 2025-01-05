package ru.yandex.practicum.kanban.repository.impls.in_file;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.repository.Repository;
import ru.yandex.practicum.kanban.repository.impls.in_file.datasource.DataQuery;
import ru.yandex.practicum.kanban.repository.impls.in_file.datasource.DataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InFileEpicRepositoryImpl implements Repository<Epic> {

    private final FileDataSource source;

    public InFileEpicRepositoryImpl(FileDataSource source) {
        this.source = source;
    }

    @Override
    public List<Epic> getAll() {
        List<Epic> result = new ArrayList<>();
        for (DataSet dataSet : source.read(new DataQuery("TYPE", "EPIC"))) {
            String idOfSubtasks = dataSet.getString("subtasks");
            if (idOfSubtasks.isBlank()) {
                result.add(fromDataSet(dataSet));
            } else {
                List<DataSet> subtasks = new ArrayList<>();
                for (Character id : idOfSubtasks.toCharArray()) {
                    subtasks.addAll(source.read(new DataQuery("TYPE", "SUBTASK"), new DataQuery("ID", id.toString())));
                }
                result.add(fromDataSet(dataSet, subtasks));
            }
        }
        return result;
    }

    @Override
    public void deleteAll() {
        DataQuery dataQuery = new DataQuery("TYPE", "EPIC");
        source.clear(dataQuery);
    }

    @Override
    public Optional<Epic> getById(int id) {
        DataQuery typeDataQuery = new DataQuery("TYPE", "EPIC");
        DataQuery idDataQuery = new DataQuery("ID", Integer.toString(id));
        List<DataSet> result = source.read(typeDataQuery, idDataQuery);
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            DataSet dataSet = result.getFirst();
            List<Integer> subtasksId = listIntegerId(dataSet.getString("subtasks"));
            if (subtasksId.isEmpty()) {
                return Optional.of(fromDataSet(dataSet));
            } else {
                List<DataSet> subtasks = new ArrayList<>();
                for (Integer subtaskId : subtasksId) {
                    subtasks.addAll(
                            source.read(new DataQuery("TYPE", "SUBTASK"), new DataQuery("ID", subtaskId.toString()))
                    );
                }
                return Optional.of(fromDataSet(dataSet, subtasks));
            }
        }
    }

    @Override
    public void deleteById(int id) {
        DataQuery typeDataQuery = new DataQuery("TYPE", "EPIC");
        DataQuery idDataQuery = new DataQuery("ID", Integer.toString(id));
        source.clear(typeDataQuery, idDataQuery);
    }

    @Override
    public void create(Epic epic) {
        DataQuery typeDataQuery = new DataQuery("TYPE", "EPIC");
        source.write(toDataSet(epic), typeDataQuery);
    }

    @Override
    public void update(Epic epic) {
        DataQuery typeDataQuery = new DataQuery("TYPE", "EPIC");
        DataQuery idDataQuery = new DataQuery("ID", Integer.toString(epic.getId()));
        source.overwrite(toDataSet(epic), typeDataQuery, idDataQuery);
    }

    private Epic fromDataSet(DataSet epicDataSet, List<DataSet> subtasksDataSet) {
        Epic epic = fromDataSet(epicDataSet);
        for (DataSet dataSet : subtasksDataSet) {
            Subtask subtask = new Subtask(dataSet.getInt("id"),
                    dataSet.getString("title"),
                    dataSet.getString("description"),
                    Status.valueOf(dataSet.getString("status")
                    ));
            subtask.addEpic(epic);
            epic.addSubtask(subtask);
        }
        return epic;
    }

    private Epic fromDataSet(DataSet dataSet) {
        return new Epic(dataSet.getInt("id"),
                dataSet.getString("title"),
                dataSet.getString("description"),
                Status.valueOf(dataSet.getString("status")));
    }

    private DataSet toDataSet(Epic epic) {
        return DataSet.builder()
                .add("id", epic.getId())
                .add("title", epic.getTitle())
                .add("description", epic.getDescription())
                .add("status", epic.getStatus().toString())
                .add("subtasks", idOfSubtasks(epic.getSubtasks()))
                .build();
    }

    private String idOfSubtasks(List<Subtask> subtasks) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Subtask subtask : subtasks) {
            sb.append(subtask.getId());
            if (subtasks.indexOf(subtask) < subtasks.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private List<Integer> listIntegerId(String idOfSubtasks) {
        List<Integer> integerList = new ArrayList<>();
        if (idOfSubtasks.length() > 2) {
            String substring = idOfSubtasks.substring(1, idOfSubtasks.length() - 1);
            String[] idArray = substring.split(",");
            for (String id : idArray) {
                integerList.add(Integer.valueOf(id));
            }
        }
        return integerList;
    }
}