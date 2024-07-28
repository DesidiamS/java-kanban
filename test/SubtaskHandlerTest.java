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

public class SubtaskHandlerTest {
    private static final String URI_CONST = "http://127.0.0.1:8080/subtasks";
    TaskManager taskManager;
    HttpTaskServer httpTaskServer;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    @BeforeEach
    public void start() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.startServer();
        Epic epic = new Epic("Тестовая задача", "Тестовое описание");
        taskManager.createEpic(epic);
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
    void isSubtaskAdded() {
        Subtask subtask = new Subtask("Тестовая задача", "Тестовое описание", 1);
        String json = gson.toJson(subtask);
        URI uri = URI.create(URI_CONST);
        try {
            HttpResponse<String> response = sendHttpRequest(uri, "POST", json);
            assertEquals(201, response.statusCode());
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void isSubtaskUpdated() {
        Subtask subtask = new Subtask("Тестовая задача", "Тестовое описание", 1);
        URI uri = URI.create(URI_CONST);
        String json = gson.toJson(subtask);
        try {
            sendHttpRequest(uri, "POST", json);
            subtask.setDescription("Измененное описание");
            uri = URI.create(URI_CONST + "/2");
            json = gson.toJson(subtask);
            HttpResponse<String> response = sendHttpRequest(uri, "POST", json);
            assertEquals(201, response.statusCode());
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void isSubtaskDeleted() {
        Subtask subtask = new Subtask("Тестовая задача", "Тестовое описание", 1);
        URI uri = URI.create(URI_CONST);
        String json = gson.toJson(subtask);
        try {
            sendHttpRequest(uri, "POST", json);
            uri = URI.create(URI_CONST + "/2");
            HttpResponse<String> response = sendHttpRequest(uri, "DELETE", "");
            assertEquals(200, response.statusCode());
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void isSubtaskGet() {
        Subtask subtask = new Subtask("Тестовая задача", "Тестовое описание", 1);
        subtask.setId(1);
        URI uri = URI.create(URI_CONST);
        String json = gson.toJson(subtask);
        try {
            sendHttpRequest(uri, "POST", json);
            uri = URI.create(URI_CONST + "/2");
            HttpResponse<String> response = sendHttpRequest(uri, "GET", "");
            assertEquals(200, response.statusCode());
        } catch (Exception e) {
            assert false;
        }
    }
}
