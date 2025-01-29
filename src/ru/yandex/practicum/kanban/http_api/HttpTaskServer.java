package ru.yandex.practicum.kanban.http_api;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.kanban.service.managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private final HttpServer httpServer;
    private final int port;
    private boolean isStarted;

    public HttpTaskServer(TaskManager taskManager, int port) throws IOException {
        this.port = port;
        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        addContexts(new BaseHttpContext(taskManager));
    }

    private void addContexts(BaseHttpContext baseHttpContext) {
        for (BaseHttpHandler handler : baseHttpContext.getHttpHandlers()) {
            httpServer.createContext(handler.getPath(), handler.getHttpHandler());
        }
    }

    public void start() {
        this.httpServer.start();
        this.isStarted = true;
    }

    public void stop() {
        this.httpServer.stop(1);
        this.isStarted = false;
    }

    public boolean getStatus() {
        return isStarted;
    }
}