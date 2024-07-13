import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatuses;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TaskTest {

    @Test
    void isTaskNotEqualsById() {
        Task task1 = new Task("Тестовая задача 1", "");
        Task task2 = new Task("Тестовая задача 1", "");
        task1.setStatus(TaskStatuses.NEW);
        task2.setStatus(TaskStatuses.NEW);
        task1.setId(1);
        task2.setId(2);
        assertNotEquals(task1, task2);
    }

    @Test
    void isEpicNotEqualsById() {
        Epic epic1 = new Epic("Тестовый эпик 1", "");
        Epic epic2 = new Epic("Тестовый эпик 1", "");
        epic1.setId(1);
        epic2.setId(2);
        epic1.setStatus(TaskStatuses.NEW);
        epic2.setStatus(TaskStatuses.NEW);
        assertNotEquals(epic1, epic2);
    }

    @Test
    void isSubtaskNotEqualsById() {
        Subtask subtask1 = new Subtask("Тестовая подзадача 1", "", 1);
        Subtask subtask2 = new Subtask("Тестовая подзадача 1", "", 1);
        subtask1.setId(1);
        subtask2.setId(2);
        subtask1.setStatus(TaskStatuses.NEW);
        subtask2.setStatus(TaskStatuses.NEW);
        assertNotEquals(subtask1, subtask2);
    }
}