package ru.yandex.practicum.kanban.repository.impls.in_file;

import ru.yandex.practicum.kanban.repository.impls.in_file.datasource.DataSet;
import ru.yandex.practicum.kanban.repository.impls.in_file.datasource.DataQuery;

import java.util.List;

public interface FileDataSource {

    List<DataSet> read(DataQuery... dataQuery);

    void clear(DataQuery... dataQuery);

    void write(DataSet newData, DataQuery... dataQuery);

    void overwrite(DataSet updatedData, DataQuery... dataQuery);
}