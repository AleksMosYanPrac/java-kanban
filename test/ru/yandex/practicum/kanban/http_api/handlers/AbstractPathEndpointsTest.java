package ru.yandex.practicum.kanban.http_api.handlers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.kanban.http_api.HttpTaskServer;
import ru.yandex.practicum.kanban.http_api.ResponseConverter;
import ru.yandex.practicum.kanban.http_api.TestHttpClient;
import ru.yandex.practicum.kanban.http_api.convertors.JsonConverterImpl;
import ru.yandex.practicum.kanban.service.managers.TaskManager;
import ru.yandex.practicum.kanban.service.util.Managers;

import java.io.IOException;
import java.net.URI;

public abstract class AbstractPathEndpointsTest {

    private static final int PORT = 8080;

    protected TaskManager taskManager;
    protected ResponseConverter converter;
    protected TestHttpClient httpClient;
    protected HttpTaskServer server;


    @BeforeEach
    void setUp() throws IOException {
        this.taskManager = Managers.getDefault();
        this.converter = new JsonConverterImpl(false);
        this.server = new HttpTaskServer(taskManager, PORT);
        this.httpClient = new TestHttpClient(URI.create("http://localhost:" + PORT));
        server.start();
    }

    @AfterEach
    void setDown() {
        server.stop();
    }
}