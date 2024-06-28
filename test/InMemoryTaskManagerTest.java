import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.Managers;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static service.Managers.getDefault;

public class InMemoryTaskManagerTest extends TaskManagersTest {

    @Override
    public InMemoryTaskManager getManager() {
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @BeforeAll
    static void isTaskManagerInitiated() {
        assertNotNull(getDefault());
    }

    @BeforeAll
    static void isHistoryManagerInitiated() {
        assertNotNull(Managers.getDefaultHistory());
    }

    @Test
    void isTaskEqualsInManagerAndInModel() {
        Task task = new Task("Задача", "Описание");
        try {
            manager.createTask(task);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
        int taskId = task.getId();
        assertEquals(task, manager.getTaskById(taskId));
    }

    @Test
    void isHistoryAdded() {
        Task task = new Task("Задача", "Описание");
        try {
            manager.createTask(task);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
        int taskId = task.getId();
        manager.getTaskById(taskId);
        assertEquals(1, manager.getHistory().size());
    }

    @Test
    void isHistoryChangedWhenUpdateTask() {
        Task task = new Task("Задача", "Описание");
        Task updatedTask = new Task("Задача 2", "Описание 2");
        try {
            manager.createTask(task);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
        int taskId = task.getId();
        manager.getTaskById(taskId);
        updatedTask.setId(taskId);
        try {
            manager.updateTask(updatedTask);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
        manager.getTaskById(taskId);
        assertEquals(1, manager.getHistory().size());
    }

    @Test
    void isTimeException() {
        Task task = new Task("Тестовая задача", "Описание", LocalDateTime.now(), Duration.ofMinutes(10L));
        Task task2 = new Task("Тестовая задача 2", "Описание 2",
                LocalDateTime.now().plus(Duration.ofMinutes(5L)), Duration.ofMinutes(10L));
        assertDoesNotThrow(() -> manager.createTask(task));
        assertThrowsExactly(TaskTimeException.class, () -> manager.createTask(task2));
    }

}
