package ru.yandex.practicum.kanban.repository.impls.in_file;

import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.repository.Repository;
import ru.yandex.practicum.kanban.repository.impls.in_file.datasource.DataQuery;
import ru.yandex.practicum.kanban.repository.impls.in_file.datasource.DataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InFileSubtaskRepositoryImpl implements Repository<Subtask> {

    private final FileDataSource source;

    public InFileSubtaskRepositoryImpl(FileDataSource dataSource) {
        this.source = dataSource;
    }

    @Override
    public List<Subtask> getAll() {
        List<Subtask> result = new ArrayList<>();
        for (DataSet dataSet : source.read(new DataQuery("TYPE", "SUBTASK"))) {
            java.lang.String epicId = dataSet.getString("epic");
            if (epicId.isBlank()) {
                result.add(fromDataSet(dataSet));
            } else {
                List<DataSet> epic = source.read(new DataQuery("TYPE", "EPIC"), new DataQuery("ID", epicId));
                result.add(fromDataSet(dataSet, epic.getFirst()));
            }
        }
        return result;
    }

    @Override
    public void deleteAll() {
        DataQuery dataQuery = new DataQuery("TYPE", "SUBTASK");
        source.clear(dataQuery);
    }

    @Override
    public Optional<Subtask> getById(int id) {
        DataQuery typeDataQuery = new DataQuery("TYPE", "SUBTASK");
        DataQuery idDataQuery = new DataQuery("ID", Integer.toString(id));
        List<DataSet> result = source.read(typeDataQuery, idDataQuery);
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            DataSet dataSet = result.getFirst();
            java.lang.String epicId = dataSet.getString("epic");
            if (epicId.isBlank()) {
                return Optional.of(fromDataSet(dataSet));
            } else {
                List<DataSet> epic = source.read(new DataQuery("TYPE", "EPIC"), new DataQuery("ID", epicId));
                return Optional.of(fromDataSet(dataSet, epic.getFirst()));
            }
        }
    }

    @Override
    public void deleteById(int id) {
        DataQuery typeDataQuery = new DataQuery("TYPE", "SUBTASK");
        DataQuery idDataQuery = new DataQuery("ID", Integer.toString(id));
        source.clear(typeDataQuery, idDataQuery);
    }

    @Override
    public void create(Subtask subtask) {
        DataQuery typeDataQuery = new DataQuery("TYPE", "SUBTASK");
        source.write(toDataSet(subtask), typeDataQuery);
    }

    @Override
    public void update(Subtask subtask) {
        DataQuery typeDataQuery = new DataQuery("TYPE", "SUBTASK");
        DataQuery idDataQuery = new DataQuery("ID", Integer.toString(subtask.getId()));
        source.overwrite(toDataSet(subtask), typeDataQuery, idDataQuery);
    }

    private Subtask fromDataSet(DataSet subtaskDataSet, DataSet epicDataSet) {
        Subtask subtask = fromDataSet(subtaskDataSet);
        TaskDTO taskDTO = new TaskDTO(epicDataSet.getInt("id"),
                epicDataSet.getString("title"),
                epicDataSet.getString("description"),
                epicDataSet.getString("status"),
                epicDataSet.getString("start_time"),
                epicDataSet.getLong("duration")
        );
        Epic epic =  new TaskBuilder().setId(taskDTO.getId()).setData(taskDTO).buildEpic();
        subtask.addEpic(epic);
        epic.addSubtask(subtask);
        return subtask;
    }

    private Subtask fromDataSet(DataSet dataSet) {
        TaskDTO taskDTO = new TaskDTO(dataSet.getInt("id"),
                dataSet.getString("title"),
                dataSet.getString("description"),
                dataSet.getString("status"),
                dataSet.getString("start_time"),
                dataSet.getLong("duration")
        );
        return new TaskBuilder().setId(taskDTO.getId()).setData(taskDTO).buildSubtask();
    }

    private DataSet toDataSet(Subtask subtask) {
        String epicId = "";
        if (Objects.nonNull(subtask.getEpic())) {
            epicId = Integer.toString(subtask.getEpic().getId());
        }
        return DataSet.builder()
                .add("id", subtask.getId())
                .add("title", subtask.getTitle())
                .add("description", subtask.getDescription())
                .add("status", subtask.getStatus().toString())
                .add("start_time", getStringStartTime(subtask))
                .add("duration", getStringDuration(subtask))
                .add("epic", epicId)
                .build();
    }
    private String getStringStartTime(Subtask subtask) {
        String starTime = "";
        if (subtask.hasStartTimeAndDuration()) {
            starTime = subtask.getStartTime().format(Task.DATE_TIME_FORMATTER);
        }
        return starTime;
    }

    private String getStringDuration(Subtask subtask) {
        String duration = "";
        if (subtask.hasStartTimeAndDuration()) {
            duration = Long.toString(subtask.getDuration().toMinutes());
        }
        return duration;
    }
}