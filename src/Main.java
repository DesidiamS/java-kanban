import model.*;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager tm = new InMemoryTaskManager(new InMemoryHistoryManager());
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(3L);

        try {
            tm.createTask(new Task("Тестовая задача", "Тестовое описание задачи"));
            tm.createTask(new Task("Тестовая задача 2", "Тестовое описание задачи 2"));
            tm.createEpic(new Epic("Тестовая эпик задача", "Тестовое описание эпик задачи"));
            tm.createSubtask(new Subtask("Тестовая подзадача", "Тестовое", 3, startTime, duration));
            tm.createSubtask(new Subtask("Тестовая подзадача", "Тестовое", 3, startTime, duration));
        } catch (TaskTimeException ignored) {
            System.out.println("Ошибка");
        }
        System.out.println(tm.getTaskById(1).getStartTime());
        System.out.println(tm.getEpicTaskById(3).getStartTime() + " and duration: " + tm.getEpicTaskById(3).getDuration().toMinutes() +
                " and endTime: " + tm.getEpicTaskById(3).getEndTime());

        System.out.println(tm.getSubTasksByEpicId(3));
    }
}