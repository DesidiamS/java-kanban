package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Endpoints;
import model.Task;
import model.TaskTimeException;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoints endpoints = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());
        switch (endpoints) {
            case GET -> getTasks(httpExchange);
            case GET_BY_ID -> getTaskById(httpExchange);
            case POST -> createTask(httpExchange);
            case PUT -> updateTask(httpExchange);
            case DELETE -> deleteTask(httpExchange);
        }
    }

    private void getTasks(HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(taskManager.getTasks());
        sendText(httpExchange, response);
    }

    private void getTaskById(HttpExchange httpExchange) throws IOException {
        int taskId = getIdFromPath(httpExchange);
        Task task = taskManager.getTaskById(taskId);
        if (task == null) {
            sendNotFound(httpExchange, "Задача не найдена");
        }
        String response = gson.toJson(task);
        sendText(httpExchange, response);
    }

    private void createTask(HttpExchange httpExchange) throws IOException {
        String request = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(request, Task.class);
        try {
            taskManager.createTask(task);
        } catch (TaskTimeException e) {
            sendHasInteractions(httpExchange, e.getMessage());
        }
        sendCreated(httpExchange, "Задача " + task.getId() + " успешно создана");
    }

    private void updateTask(HttpExchange httpExchange) throws IOException {
        String request = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(request, Task.class);
        task.setId(getIdFromPath(httpExchange));
        if (taskManager.getTaskById(task.getId()) == null) {
            sendNotFound(httpExchange, "Задача для обновления не найдена!");
            return;
        }
        try {
            taskManager.updateTask(task);
        } catch (TaskTimeException e) {
            sendHasInteractions(httpExchange, e.getMessage());
        }
        sendCreated(httpExchange, "Задача " + task.getId() + " успешно обновлена");
    }

    private void deleteTask(HttpExchange httpExchange) throws IOException {
        int taskId = getIdFromPath(httpExchange);
        if (taskManager.getTaskById(taskId) == null) {
            sendNotFound(httpExchange, "Задача для удаления не найдена!");
            return;
        }
        taskManager.deleteTaskById(taskId);
        sendText(httpExchange, "Задача " + taskId + " удалена");
    }
}
