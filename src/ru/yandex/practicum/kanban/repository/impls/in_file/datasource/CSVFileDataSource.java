package ru.yandex.practicum.kanban.repository.impls.in_file.datasource;

import ru.yandex.practicum.kanban.repository.impls.in_file.exceptions.FileReadException;
import ru.yandex.practicum.kanban.repository.impls.in_file.exceptions.FileWriteException;
import ru.yandex.practicum.kanban.repository.impls.in_file.FileDataSource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.nio.file.StandardOpenOption.*;

public class CSVFileDataSource implements FileDataSource {

    private final Path path;
    private List<Line> lines;

    public CSVFileDataSource(Path path) {
        this.path = path;
        this.lines = readFile();
    }

    @Override
    public final List<DataSet> read(DataQuery... queries) {
        List<DataSet> result = new ArrayList<>();
        for (Line line : lines) {
            if (line.match(queries)) {
                result.add(line.arrangeToDataSet());
            }
        }
        return result;
    }

    @Override
    public void clear(DataQuery... queries) {
        List<Line> result = new ArrayList<>();
        for (Line line : lines) {
            if (line.match(queries)) {
                result.add(line);
            }
        }
        if (lines.removeAll(result)) {
            writeFile();
        }
    }

    @Override
    public void write(DataSet newData, DataQuery... queries) {
        lines.add(new Line(lines.size(), newData, queries));
        writeFile();
    }

    @Override
    public void overwrite(DataSet updatedData, DataQuery... queries) {
        Line result = null;
        for (Line line : lines) {
            if (line.match(queries)) {
                result = line;
            }
        }
        if (Objects.nonNull(result)) {
            int index = lines.indexOf(result);
            lines.set(index, new Line(result.getLineNumber(), updatedData, queries));
            writeFile();
        }
    }

    private List<Line> readFile() {
        List<Line> lines = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            int lineNumber = 0;
            if (br.ready()) {
                while (br.ready()) {
                    lines.add(new Line(lineNumber, br.readLine()));
                    lineNumber++;
                }
            } else {
                lines.add(new Line(lineNumber));
            }
        } catch (IOException e) {
            throw new FileReadException("Can't read file:" + path, e);
        }
        return lines;
    }

    private void writeFile() {
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8, WRITE, TRUNCATE_EXISTING)) {
            for (Line line : lines) {
                bw.write(line.arrangeByColumnName());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new FileWriteException("Can't write file" + path, e);
        }
    }

    private static class Line {

        private final String delimiter = ",";
        private final List<String> columnLabels =
                List.of("id", "type", "title", "status", "description","start_time","duration", "epic", "subtasks");
        private int lineNumber;
        private Map<String, String> data;

        public Line(int lineNumber) {
            this.lineNumber = lineNumber;
            this.data = new HashMap<>();
            for (String label : columnLabels) {
                data.put(label, label);
            }
        }

        public Line(int lineNumber, String data) {
            this.lineNumber = lineNumber;
            this.data = new HashMap<>();
            String[] values = data.split(delimiter, columnLabels.size());
            for (int i = 0; i < columnLabels.size(); i++) {
                this.data.put(columnLabels.get(i), values[i]);
            }
        }

        public Line(int lineNumber, DataSet dataSet, DataQuery... queries) {
            this.lineNumber = lineNumber;
            this.data = new HashMap<>();
            for (String label : columnLabels) {
                this.data.put(label, dataSet.getString(label));
            }
            for (DataQuery dataQuery : queries) {
                if (columnLabels.contains(dataQuery.getKey())) {
                    this.data.put(dataQuery.getKey(), dataQuery.getValue());
                }
            }
        }

        public String arrangeByColumnName() {
            StringBuilder sb = new StringBuilder();
            for (String label : columnLabels) {
                sb.append(data.get(label));
                if (isNotLastElement(label)) {
                    sb.append(delimiter);
                }
            }
            return sb.toString();
        }

        public DataSet arrangeToDataSet() {
            return new DataSet(data);
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public boolean match(DataQuery... queries) {
            int counter = 0;
            for (DataQuery dataQuery : queries) {
                String value = data.getOrDefault(dataQuery.getKey(), "");
                if (value.equalsIgnoreCase(dataQuery.getValue())) {
                    counter++;
                }
            }
            return counter == queries.length;
        }

        private boolean isNotLastElement(String label) {
            return columnLabels.indexOf(label) < columnLabels.size() - 1;
        }
    }
}