package ru.yandex.practicum.kanban;

import ru.yandex.practicum.kanban.http_api.HttpTaskServer;
import ru.yandex.practicum.kanban.service.managers.TaskManager;
import ru.yandex.practicum.kanban.service.util.Managers;

import java.io.IOException;
import java.util.Scanner;

public class KanbanManagerHttpServerMain {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        TaskManager taskManager = Managers.defaultTaskManagerWithoutCopyInHistory();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager, PORT);

        httpTaskServer.start();
        System.out.println("Server started on PORT:" + PORT);

        while (true) {
            System.out.println("To stop server enter: 0");
            String action = scanner.next();
            switch (action) {
                case "0":
                    httpTaskServer.stop();
                    System.out.println("Server is stop");
                    return;
                default:
                    System.out.println("Неизвестная команда");
                    break;
            }
        }
    }
}