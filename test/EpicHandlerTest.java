import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.HttpTaskServer;
import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EpicHandlerTest {
    TaskManager taskManager;
    HttpTaskServer httpTaskServer;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    private static final String URI_CONST = "http://127.0.0.1:8080/epics";

    @BeforeEach
    public void start() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.startServer();
    }

    @AfterEach
    public void stop() {
        httpTaskServer.stopServer();
    }

    HttpResponse<String> sendHttpRequest(URI url, String method, String body) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).method(method, HttpRequest.BodyPublishers.ofString(body)).build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void isEpicAdded() {
        Epic epic = new Epic("Тестовая задача", "Тестовое описание");
        String json = gson.toJson(epic);
        URI uri = URI.create(URI_CONST);
        try {
            HttpResponse<String> response = sendHttpRequest(uri, "POST", json);
            assertEquals(201, response.statusCode());
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void isEpicUpdated() {
        Epic epic = new Epic("Тестовая задача", "Тестовое описание");
        URI uri = URI.create(URI_CONST);
        String json = gson.toJson(epic);
        try {
            sendHttpRequest(uri, "POST", json);
            epic.setDescription("Измененное описание");
            uri = URI.create(URI_CONST + "/1");
            json = gson.toJson(epic);
            HttpResponse<String> response = sendHttpRequest(uri, "POST", json);
            assertEquals(201, response.statusCode());
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void isEpicDeleted() {
        Epic epic = new Epic("Тестовая задача", "Тестовое описание");
        URI uri = URI.create(URI_CONST);
        String json = gson.toJson(epic);
        try {
            sendHttpRequest(uri, "POST", json);
            uri = URI.create(URI_CONST + "/1");
            HttpResponse<String> response = sendHttpRequest(uri, "DELETE", "");
            assertEquals(200, response.statusCode());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assert false;
        }
    }

    @Test
    void isEpicGet() {
        Epic epic = new Epic("Тестовая задача", "Тестовое описание");
        epic.setId(1);
        URI uri = URI.create(URI_CONST);
        String json = gson.toJson(epic);
        try {
            sendHttpRequest(uri, "POST", json);
            uri = URI.create(URI_CONST + "/1");
            HttpResponse<String> response = sendHttpRequest(uri, "GET", "");
            assertEquals(200, response.statusCode());
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void isSubtaskGetByEpicId() {
        Epic epic = new Epic("Тестовая задача", "Тестовое описание");
        Subtask subtask = new Subtask("Тестовая задача", "Тестовое описание", 1);
        String json = gson.toJson(epic);
        URI uri = URI.create(URI_CONST);
        try {
            sendHttpRequest(uri, "POST", json);
            json = gson.toJson(subtask);
            uri = URI.create("http://127.0.0.1:8080/subtaks");
            sendHttpRequest(uri, "POST", json);
            uri = URI.create(URI_CONST + "/1/subtasks");
            HttpResponse<String> response = sendHttpRequest(uri, "GET", "");
            assertNotNull(response.body());
        } catch (Exception e) {
            assert false;
        }
    }
}
