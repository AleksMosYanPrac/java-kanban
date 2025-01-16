package ru.yandex.practicum.kanban.repository.impls.in_file;

import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.repository.Repository;
import ru.yandex.practicum.kanban.repository.impls.in_file.datasource.DataQuery;
import ru.yandex.practicum.kanban.repository.impls.in_file.datasource.DataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InFileTaskRepositoryImpl implements Repository<Task> {

    private final FileDataSource source;

    public InFileTaskRepositoryImpl(FileDataSource source) {
        this.source = source;
    }

    @Override
    public List<Task> getAll() {
        List<Task> result = new ArrayList<>();
        for (DataSet dataSet : source.read(new DataQuery("TYPE", "TASK"))) {
            result.add(fromDataSet(dataSet));
        }
        return result;
    }

    @Override
    public void deleteAll() {
        DataQuery dataQuery = new DataQuery("TYPE", "TASK");
        source.clear(dataQuery);
    }

    @Override
    public Optional<Task> getById(int id) {
        DataQuery typeDataQuery = new DataQuery("TYPE", "TASK");
        DataQuery idDataQuery = new DataQuery("ID", Integer.toString(id));
        List<DataSet> result = source.read(typeDataQuery, idDataQuery);
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(fromDataSet(result.getFirst()));
        }
    }

    @Override
    public void deleteById(int id) {
        DataQuery typeDataQuery = new DataQuery("TYPE", "TASK");
        DataQuery idDataQuery = new DataQuery("ID", Integer.toString(id));
        source.clear(typeDataQuery, idDataQuery);
    }

    @Override
    public void create(Task task) {
        DataQuery typeDataQuery = new DataQuery("TYPE", "TASK");
        source.write(toDataSet(task), typeDataQuery);
    }

    @Override
    public void update(Task task) {
        DataQuery typeDataQuery = new DataQuery("TYPE", "TASK");
        DataQuery idDataQuery = new DataQuery("ID", Integer.toString(task.getId()));
        source.overwrite(toDataSet(task), typeDataQuery, idDataQuery);
    }

    private Task fromDataSet(DataSet dataSet) {
        TaskDTO taskDTO = new TaskDTO(dataSet.getInt("id"),
                dataSet.getString("title"),
                dataSet.getString("description"),
                dataSet.getString("status"),
                dataSet.getString("start_time"),
                dataSet.getLong("duration")
        );
        return new TaskBuilder().setId(taskDTO.getId()).setData(taskDTO).buildTask();
    }

    private DataSet toDataSet(Task task) {
        return DataSet.builder()
                .add("id", task.getId())
                .add("title", task.getTitle())
                .add("description", task.getDescription())
                .add("status", task.getStatus().toString())
                .add("start_time", getStringStartTime(task))
                .add("duration", getStringDuration(task))
                .build();
    }
    private String getStringStartTime(Task task) {
        String starTime = "";
        if (task.hasStartTimeAndDuration()) {
            starTime = task.getStartTime().format(Task.DATE_TIME_FORMATTER);
        }
        return starTime;
    }

    private String getStringDuration(Task task) {
        String duration = "";
        if (task.hasStartTimeAndDuration()) {
            duration = Long.toString(task.getDuration().toMinutes());
        }
        return duration;
    }
}