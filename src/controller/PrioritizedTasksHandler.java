package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;

public class PrioritizedTasksHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public PrioritizedTasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(taskManager.getPrioritizedTasks());
        sendText(httpExchange, response);
    }
}
