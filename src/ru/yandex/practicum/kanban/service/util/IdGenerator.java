package ru.yandex.practicum.kanban.service.util;

public class IdGenerator {

    private static int counter = 1;

    private IdGenerator() {
    }

    public static int generate() {
        int id = counter;
        counter++;
        return id;
    }
}