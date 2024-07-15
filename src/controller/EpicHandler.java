package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Endpoints;
import model.Epic;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoints endpoints = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());
        switch (endpoints) {
            case GET -> getEpics(httpExchange);
            case GET_BY_ID -> getEpicById(httpExchange);
            case GET_SUBTASKS_BY_EPIC -> getSubtasksByEpic(httpExchange);
            case POST -> createEpic(httpExchange);
            case PUT -> updateEpic(httpExchange);
            case DELETE -> deleteEpic(httpExchange);
            case UNKNOWN -> sendNotAllowed(httpExchange);
        }
    }

    private void getEpics(HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(taskManager.getEpicTasks());
        sendText(httpExchange, response);
    }

    private void getEpicById(HttpExchange httpExchange) throws IOException {
        int epicId = getIdFromPath(httpExchange);
        Epic epic = taskManager.getEpicTaskById(epicId);
        if (epic == null) {
            sendNotFound(httpExchange, "Эпик не найден");
            return;
        }
        String response = gson.toJson(epic);
        sendText(httpExchange, response);
    }

    private void getSubtasksByEpic(HttpExchange httpExchange) throws IOException {
        int epicId = getIdFromPath(httpExchange);
        if (taskManager.getEpicTaskById(epicId) == null) {
            sendNotFound(httpExchange, "Эпик не найден");
            return;
        }
        String response = gson.toJson(taskManager.getSubTasksByEpicId(epicId));
        sendText(httpExchange, response);
    }

    private void createEpic(HttpExchange httpExchange) throws IOException {
        String request = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(request, Epic.class);
        taskManager.createEpic(epic);
        sendCreated(httpExchange, "Эпик " + epic.getId() + " успешно создан");
    }

    private void updateEpic(HttpExchange httpExchange) throws IOException {
        String request = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(request, Epic.class);
        epic.setId(getIdFromPath(httpExchange));
        if (taskManager.getEpicTaskById(epic.getId()) == null) {
            sendNotFound(httpExchange, "Эпик для обновления не найден!");
            return;
        }
        taskManager.updateEpic(epic);
        sendCreated(httpExchange, "Эпик " + epic.getId() + " успешно обновлен");
    }

    private void deleteEpic(HttpExchange httpExchange) throws IOException {
        int epicId = getIdFromPath(httpExchange);
        if (taskManager.getEpicTaskById(epicId) == null) {
            sendNotFound(httpExchange, "Эпик для удаления не найден!");
            return;
        }
        taskManager.deleteEpicTaskById(epicId);
        sendText(httpExchange, "Эпик " + epicId + " удален");
    }

}
