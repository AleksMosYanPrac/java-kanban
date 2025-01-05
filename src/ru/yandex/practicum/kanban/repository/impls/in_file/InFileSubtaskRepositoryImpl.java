package ru.yandex.practicum.kanban.repository.impls.in_file;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Status;
import ru.yandex.practicum.kanban.model.Subtask;
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
            String epicId = dataSet.getString("epic");
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
            String epicId = dataSet.getString("epic");
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
        Epic epic = new Epic(epicDataSet.getInt("id"),
                epicDataSet.getString("title"),
                epicDataSet.getString("description"),
                Status.valueOf(epicDataSet.getString("status")
                ));
        subtask.addEpic(epic);
        epic.addSubtask(subtask);
        return subtask;
    }

    private Subtask fromDataSet(DataSet dataSet) {
        return new Subtask(dataSet.getInt("id"),
                dataSet.getString("title"),
                dataSet.getString("description"),
                Status.valueOf(dataSet.getString("status")));
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
                .add("epic", epicId)
                .build();
    }
}