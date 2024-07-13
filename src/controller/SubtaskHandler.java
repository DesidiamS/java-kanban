package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Endpoints;
import model.Subtask;
import model.TaskTimeException;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoints endpoints = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());
        switch (endpoints) {
            case GET -> getSubtasks(httpExchange);
            case GET_BY_ID -> getSubtaskById(httpExchange);
            case POST -> createSubtask(httpExchange);
            case PUT -> updateSubtask(httpExchange);
            case DELETE -> deleteSubtask(httpExchange);
        }
    }

    private void getSubtasks(HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(taskManager.getSubTasks());
        sendText(httpExchange, response);
    }

    private void getSubtaskById(HttpExchange httpExchange) throws IOException {
        int subtaskId = getIdFromPath(httpExchange);
        Subtask subtask = taskManager.getSubTaskById(subtaskId);
        if (subtask == null) {
            sendNotFound(httpExchange, "Подзадача не найдена");
            return;
        }
        String response = gson.toJson(subtask);
        sendText(httpExchange, response);
    }

    private void createSubtask(HttpExchange httpExchange) throws IOException {
        String request = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(request, Subtask.class);
        try {
            taskManager.createSubtask(subtask);
        } catch (TaskTimeException e) {
            sendHasInteractions(httpExchange, e.getMessage());
        }
        sendCreated(httpExchange, "Подзадача " + subtask.getId() + " успешно создана");
    }

    private void updateSubtask(HttpExchange httpExchange) throws IOException {
        String request = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(request, Subtask.class);
        subtask.setId(getIdFromPath(httpExchange));
        if (taskManager.getSubTaskById(subtask.getId()) == null) {
            sendNotFound(httpExchange, "Подзадача для обновления не найдена!");
            return;
        }
        try {
            taskManager.updateSubtask(subtask);
        } catch (TaskTimeException e) {
            sendHasInteractions(httpExchange, e.getMessage());
        }
        sendCreated(httpExchange, "Подзадача " + subtask.getId() + " успешно обновлена");
    }

    private void deleteSubtask(HttpExchange httpExchange) throws IOException {
        int subtaskId = getIdFromPath(httpExchange);
        if (taskManager.getSubTaskById(subtaskId) == null) {
            sendNotFound(httpExchange, "Подзадача для удаления не найдена!");
            return;
        }
        taskManager.deleteSubTaskById(subtaskId);
        sendText(httpExchange, "Подзадача " + subtaskId + " удалена");
    }
}
