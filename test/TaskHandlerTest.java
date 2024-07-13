import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.HttpTaskServer;
import model.Task;
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

public class TaskHandlerTest {

    TaskManager taskManager;
    HttpTaskServer httpTaskServer;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    private static final String URI_CONST = "http://127.0.0.1:8080/tasks";

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
    void isTaskAdded() {
        Task task = new Task("Тестовая задача", "Тестовое описание", LocalDateTime.now(),
                Duration.ofMinutes(10));
        String json = gson.toJson(task);
        URI uri = URI.create(URI_CONST);
        try {
            HttpResponse<String> response = sendHttpRequest(uri, "POST", json);
            assertEquals(201, response.statusCode());
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void isTimeOverlap() {
        Task task = new Task("Тестовая задача", "Тестовое описание", LocalDateTime.now(),
                Duration.ofMinutes(10));
        Task task1 = new Task("Тестовая задача 2", "Тестовое описание 2", LocalDateTime.now(),
                Duration.ofMinutes(5));
        URI uri = URI.create(URI_CONST);
        try {
            String json = gson.toJson(task);
            sendHttpRequest(uri, "POST", json);
            json = gson.toJson(task1);
            HttpResponse<String> response = sendHttpRequest(uri, "POST", json);
            assertEquals(406, response.statusCode());
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void isTaskUpdated() {
        Task task = new Task("Тестовая задача", "Тестовое описание", LocalDateTime.now(),
                Duration.ofMinutes(10));
        URI uri = URI.create(URI_CONST);
        String json = gson.toJson(task);
        try {
            sendHttpRequest(uri, "POST", json);
            task.setDescription("Измененное описание");
            uri = URI.create(URI_CONST + "/1");
            json = gson.toJson(task);
            HttpResponse<String> response = sendHttpRequest(uri, "POST", json);
            assertEquals(201, response.statusCode());
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void isTaskDeleted() {
        Task task = new Task("Тестовая задача", "Тестовое описание", LocalDateTime.now(),
                Duration.ofMinutes(10));
        URI uri = URI.create(URI_CONST);
        String json = gson.toJson(task);
        try {
            sendHttpRequest(uri, "POST", json);
            uri = URI.create(URI_CONST + "/1");
            HttpResponse<String> response = sendHttpRequest(uri, "DELETE", "");
            assertEquals(200, response.statusCode());
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    void isTaskGet() {
        Task task = new Task("Тестовая задача", "Тестовое описание", LocalDateTime.now(),
                Duration.ofMinutes(10));
        task.setId(1);
        URI uri = URI.create(URI_CONST);
        String json = gson.toJson(task);
        try {
            sendHttpRequest(uri, "POST", json);
            uri = URI.create(URI_CONST + "/1");
            HttpResponse<String> response = sendHttpRequest(uri, "GET", "");
            assertEquals(json, response.body());
        } catch (Exception e) {
            assert false;
        }
    }
}
