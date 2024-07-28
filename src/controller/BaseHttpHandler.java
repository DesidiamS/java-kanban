package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Endpoints;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(404, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(406, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendCreated(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(201, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotAllowed(HttpExchange h) throws IOException {
        h.sendResponseHeaders(405, 0);
        h.close();
    }

    public Endpoints getEndpoint(String path, String method) {
        String[] pathPart = path.split("/");
        switch (method) {
            case "GET":
                if (pathPart.length == 2) {
                    return Endpoints.GET;
                } else if (pathPart.length == 3) {
                    return Endpoints.GET_BY_ID;
                } else if (pathPart.length == 4 && pathPart[3].equals("subtasks")) {
                    return Endpoints.GET_SUBTASKS_BY_EPIC;
                } else {
                    return Endpoints.UNKNOWN;
                }
            case "POST":
                if (pathPart.length == 2) {
                    return Endpoints.POST;
                } else if (pathPart.length == 3) {
                    return Endpoints.PUT;
                } else {
                    return Endpoints.UNKNOWN;
                }
            case "DELETE":
                return Endpoints.DELETE;
            default:
                return Endpoints.UNKNOWN;
        }
    }

    protected int getIdFromPath(HttpExchange httpExchange) {
        return Integer.parseInt(httpExchange.getRequestURI().getPath().split("/")[2]);
    }
}