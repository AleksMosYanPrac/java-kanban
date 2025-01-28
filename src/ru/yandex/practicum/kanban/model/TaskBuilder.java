package ru.yandex.practicum.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class TaskBuilder {

    private int id;
    private String title;
    private String description;
    private Status status;
    private LocalDateTime startTime;
    private Duration duration;

    public TaskBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public TaskBuilder setData(TaskDTO data) {
        this.title = data.getTitle();
        this.description = data.getDescription();
        this.status = parseStatus(data.getStatus());
        if (Objects.nonNull(data.getStartTime())) {
            this.startTime = parseStartTime(data.getStartTime());
            this.duration = parseDuration(data.getDurationInMinutes());
        }
        return this;
    }

    public Task buildTask() {
        return Objects.nonNull(startTime) && Objects.nonNull(duration) ?
                new Task(id, title, description, status, startTime, duration) :
                new Task(id, title, description, status);
    }

    public Subtask buildSubtask() {
        return Objects.nonNull(startTime) && Objects.nonNull(duration) ?
                new Subtask(id, title, description, status, startTime, duration) :
                new Subtask(id, title, description, status);
    }

    public Epic buildEpic() {
        return Objects.nonNull(startTime) && Objects.nonNull(duration) ?
                new Epic(id, title, description, status, startTime, duration) :
                new Epic(id, title, description, status);
    }

    private Duration parseDuration(long durationInMinutes) throws IllegalArgumentException {
        Duration duration = null;
        if (durationInMinutes > 0) {
            duration = Duration.ofMinutes(durationInMinutes);
        }
        return duration;
    }

    private LocalDateTime parseStartTime(String startTime) throws IllegalArgumentException {
        LocalDateTime dateTime = null;
        try {
            if (!startTime.isBlank() && !startTime.equals("null")) {
                dateTime = LocalDateTime.parse(startTime, Task.DATE_TIME_FORMATTER);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Start time: " + startTime + " should be formatted as " + Task.DATE_TIME_PATTERN
            );
        }
        return dateTime;
    }

    private Status parseStatus(String status) throws IllegalArgumentException {
        return Status.valueOf(status);
    }

    public static class CSVParser {

        private static String TITLES = "id,type,title,status,description,start_time,duration,epic,subtasks";
        private static int COLUMNS_NUMBER = 8;
        private static String SEPARATOR = ",";

        public static int getIdFromCSVLine(String scvLine) {
            String[] values = scvLine.split(SEPARATOR, COLUMNS_NUMBER);
            return Integer.parseInt(values[0]);
        }

        //EPIC
        public static boolean isEpic(String scvLine) {
            String[] values = scvLine.split(SEPARATOR, COLUMNS_NUMBER);
            return values[1].equals("EPIC");
        }

        public static Map<Integer, Epic> parseEpicsFromCSVLines(List<String> lines) {
            Map<Integer, Epic> data = new HashMap<>();
            for (String line : lines) {
                String[] values = line.split(SEPARATOR, COLUMNS_NUMBER);
                if (isEpic(line)) {
                    TaskDTO taskDTO = fromCSVLine(line);
                    Epic epic = new TaskBuilder().setId(taskDTO.getId()).setData(taskDTO).buildEpic();
                    List<Subtask> subtasks = findSubtaskInCSVLinesById(lines, values[8]);
                    if (!subtasks.isEmpty()) {
                        for (Subtask subtask : subtasks) {
                            epic.addSubtask(subtask);
                        }
                    }
                    data.put(epic.getId(), epic);
                }
            }
            return data;
        }

        public static String epicToCSVLine(Epic epic) {
            StringBuilder idOfSubtasks = new StringBuilder();
            List<Subtask> subtasks = epic.getSubtasks();
            idOfSubtasks.append("[");
            for (Subtask subtask : subtasks) {
                idOfSubtasks.append(subtask.getId());
                if (subtasks.indexOf(subtask) < subtasks.size() - 1) {
                    idOfSubtasks.append(",");
                }
            }
            idOfSubtasks.append("]");
            return String.format("%s,EPIC,%s,%s,%s,%s,%s,,%s",
                    epic.getId(),
                    epic.getTitle(),
                    epic.getStatus().toString(),
                    epic.getDescription(),
                    getStringStartTime(epic),
                    getStringDuration(epic),
                    idOfSubtasks.toString());
        }

        //SUBTASKS
        public static boolean isSubtask(String scvLine) {
            String[] values = scvLine.split(SEPARATOR, COLUMNS_NUMBER);
            return values[1].equals("SUBTASK");
        }

        public static Map<Integer, Subtask> parseSubtasksFromCSVLines(List<String> lines) {
            Map<Integer, Subtask> data = new HashMap<>();
            for (String line : lines) {
                if (isSubtask(line)) {
                    TaskDTO taskDTO = fromCSVLine(line);
                    Subtask subtask = new TaskBuilder().setId(taskDTO.getId()).setData(taskDTO).buildSubtask();
                    if (containsEpic(line)) {
                        Epic epic = findEpicInCSVLinesById(lines, line);
                        subtask.addEpic(epic);
                    }
                    data.put(subtask.getId(), subtask);
                }
            }
            return data;
        }

        public static String subtaskToCSVLine(Subtask subtask) {
            String epicId = "";
            if (Objects.nonNull(subtask.getEpic())) {
                epicId = Integer.toString(subtask.getEpic().getId());
            }
            return String.format("%s,SUBTASK,%s,%s,%s,%s,%s,%s,",
                    subtask.getId(),
                    subtask.getTitle(),
                    subtask.getStatus().toString(),
                    subtask.getDescription(),
                    getStringStartTime(subtask),
                    getStringDuration(subtask),
                    epicId);
        }

        //TASKS
        public static boolean isTask(String scvLine) {
            String[] values = scvLine.split(SEPARATOR, COLUMNS_NUMBER);
            return values[1].equals("TASK");
        }

        public static Map<Integer, Task> parseTasksFromCSVLines(List<String> lines) {
            Map<Integer, Task> data = new HashMap<>();
            for (String line : lines) {
                if (isTask(line)) {
                    TaskDTO taskDTO = fromCSVLine(line);
                    Task task = new TaskBuilder().setId(taskDTO.getId()).setData(taskDTO).buildTask();
                    data.put(task.getId(), task);
                }
            }
            return data;
        }

        public static String taskToCSVLine(Task task) {
            return String.format("%s,TASK,%s,%s,%s,%s,%s,,",
                    task.getId(),
                    task.getTitle(),
                    task.getStatus().toString(),
                    task.getDescription(),
                    getStringStartTime(task),
                    getStringDuration(task)
            );
        }

        private static List<Subtask> findSubtaskInCSVLinesById(List<String> lines, String lineWithIdSubtasks) {
            String idOfSubtasks = lineWithIdSubtasks.split(SEPARATOR, COLUMNS_NUMBER)[8];
            List<Subtask> subtasks = new ArrayList<>();
            if (idOfSubtasks.length() > 2) {
                String substring = idOfSubtasks.substring(1, idOfSubtasks.length() - 1);
                String[] idArray = substring.split(SEPARATOR);
                for (String id : idArray) {
                    for (String line : lines) {
                        String[] values = line.split(SEPARATOR, COLUMNS_NUMBER);
                        if (values[0].equals(id) & isSubtask(line)) {
                            TaskDTO taskDTO = fromCSVLine(line);
                            Subtask subtask = new TaskBuilder().setId(taskDTO.getId()).setData(taskDTO).buildSubtask();
                            subtasks.add(subtask);
                        }
                    }
                }
            }
            return subtasks;
        }

        private static Epic findEpicInCSVLinesById(List<String> lines, String lineWithIdEpic) {
            String id = lineWithIdEpic.split(SEPARATOR, COLUMNS_NUMBER)[7];
            Epic epic = null;
            String subtasksId = "";
            for (String line : lines) {
                String[] values = line.split(SEPARATOR, COLUMNS_NUMBER);
                if (values[0].equals(id) && isEpic(line)) {
                    TaskDTO taskDTO = fromCSVLine(line);
                    epic = new TaskBuilder().setId(taskDTO.getId()).setData(taskDTO).buildEpic();
                    subtasksId = values[8];
                }
            }
            if (Objects.nonNull(epic)) {
                List<Subtask> subtasksForEpic = findSubtaskInCSVLinesById(lines, subtasksId);
                for (Subtask subtask : subtasksForEpic) {
                    epic.addSubtask(subtask);
                    subtask.addEpic(epic);
                }
            }
            return epic;
        }

        private static boolean containsEpic(String line) {
            return !line.split(SEPARATOR, COLUMNS_NUMBER)[7].isBlank();
        }

        private static String getStringStartTime(Task task) {
            String starTime = "";
            if (task.hasStartTimeAndDuration()) {
                starTime = task.getStartTime().format(Task.DATE_TIME_FORMATTER);
            }
            return starTime;
        }

        private static String getStringDuration(Task task) {
            String duration = "";
            if (task.hasStartTimeAndDuration()) {
                duration = Long.toString(task.getDuration().toMinutes());
            }
            return duration;
        }

        private static TaskDTO fromCSVLine(String csvLine) {
            String[] values = csvLine.split(SEPARATOR, COLUMNS_NUMBER);
            long duration = values[7].isBlank() ? 0 : Long.parseLong(values[7]);
            return new TaskDTO(
                    Integer.parseInt(values[0]),
                    values[2],
                    values[4],
                    values[5],
                    values[6],
                    duration
            );
        }
    }
}