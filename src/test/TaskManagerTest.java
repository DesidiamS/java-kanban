package test;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatuses;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {
    TaskManager manager;

    @BeforeAll
    static void isTaskManagerInitiated() {
        assertNotNull(new Managers().getDefault());
    }

    @BeforeAll
    static void isHistoryManagerInitiated() {
        assertNotNull(Managers.getDefaultHistory());
    }

    @BeforeEach
    void createTaskManager() {
        manager = new Managers().getDefault();
    }

    @Test
    void isTaskCreated() {
        Task task = new Task("Задача", "");
        manager.createTask(task);
        assertTrue(manager.getTasks().size() > 0);
    }

    @Test
    void isEpicCreated() {
        Epic epic = new Epic("Эпик", "");
        manager.createEpic(epic);
        assertTrue(manager.getEpicTasks().size() > 0);
    }

    @Test
    void isSubtaskCreated() {
        Epic epic = new Epic("Эпик для подзадачи", "");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "", 1);
        manager.createSubtask(subtask);
        assertTrue(manager.getSubTasks().size() > 0);
    }

    @Test
    void isTaskUpdated() {
        Task task = new Task("Задача", "");
        String expectedName = "Задача 1";
        manager.createTask(task);
        // Постарался максимально разделить все вызовы методов подряд через точку. Наплодилось кучу переменных,
        // но надеюсь, так лучше
        int actualId = task.getId();
        Task updatedTask = new Task(expectedName, "");
        updatedTask.setId(actualId);
        manager.updateTask(updatedTask);
        Task actualTask = manager.getTaskById(actualId);
        String actualName = actualTask.getName();
        assertEquals(expectedName, actualName);
    }

    @Test
    void isEpicUpdated() {
        Epic epic = new Epic("Эпик", "");
        String expectedName = "Эпик 1";
        manager.createEpic(epic);
        int actualId = epic.getId();
        Epic updatedEpic = new Epic(expectedName, "");
        updatedEpic.setId(actualId);
        manager.updateEpic(updatedEpic);
        Epic actualEpic = manager.getEpicTaskById(actualId);
        String actualName = actualEpic.getName();
        assertEquals(expectedName, actualName);
    }

    @Test
    void isSubtaskUpdated() {
        Epic epic = new Epic("Эпик для подзадачи", "");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "", epic.getId());
        String expectedName = "Подзадача 1";
        manager.createSubtask(subtask);
        int actualId = subtask.getId();
        Subtask updatedSubtask = new Subtask(expectedName, "", epic.getId());
        updatedSubtask.setId(actualId);
        manager.updateSubtask(updatedSubtask);
        Subtask actualSubtask = manager.getSubTaskById(actualId);
        String actualName = actualSubtask.getName();
        assertEquals(expectedName, actualName);
    }

    @Test
    void isSubtaskDoNotSelfAddedInEpic() {
        Epic epic = new Epic("Эпик", "");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "", 2); // 2 - id этой же подзадачи
        manager.createSubtask(subtask);
        assertNull(manager.getSubTaskById(subtask.getId()));
    }

    @Test
    void checkSubtasksInEpic() {
        Epic epic = new Epic("Тестовый эпик", "");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "", epic.getId());
        manager.createSubtask(subtask);
        assertEquals(manager.getSubTasksByEpicId(epic.getId()), manager.getSubTasks());
    }

    @Test
    void isIdNotConflict() {
        Task task = new Task("Задача", "");
        Task task1 = new Task("Задача 1", "");
        manager.createTask(task);
        task1.setId(task.getId());
        manager.createTask(task1);
        assertEquals(2, manager.getTasks().size());
    }

    @Test
    void isTaskFindById() {
        Task task = new Task("Задача", "");
        manager.createTask(task);
        int taskId = task.getId();
        assertEquals(task, manager.getTaskById(taskId));
    }

    @Test
    void isEpicFindById() {
        Epic epic = new Epic("Эпик", "");
        manager.createEpic(epic);
        int epicId = epic.getId();
        assertEquals(epic, manager.getEpicTaskById(epicId));
    }

    @Test
    void isSubtaskFindById() {
        Epic epic = new Epic("Эпик", "");
        manager.createEpic(epic);
        int epicId = epic.getId();
        Subtask subtask = new Subtask("Подзадача", "", epicId);
        manager.createSubtask(subtask);
        int subtaskId = subtask.getId();
        assertEquals(subtask, manager.getSubTaskById(subtaskId));
    }

    @Test
    void isTaskEqualsInManagerAndInModel() {
        Task task = new Task("Задача", "Описание");
        manager.createTask(task);
        int taskId = task.getId();
        assertEquals(task, manager.getTaskById(taskId));
    }

    @Test
    void isHistoryAdded() {
        Task task = new Task("Задача", "Описание");
        manager.createTask(task);
        int taskId = task.getId();
        manager.getTaskById(taskId);
        assertEquals(1, manager.getHistory().size());
    }

    @Test
    void isHistoryChangedWhenUpdateTask() {
        Task task = new Task("Задача", "Описание");
        Task updatedTask = new Task("Задача 2", "Описание 2");
        manager.createTask(task);
        int taskId = task.getId();
        manager.getTaskById(taskId);
        updatedTask.setId(taskId);
        manager.updateTask(updatedTask);
        manager.getTaskById(taskId);
        List<Task> expectedHistory = new ArrayList<>();
        expectedHistory.add(task);
        expectedHistory.add(updatedTask);
        assertEquals(expectedHistory, manager.getHistory());
    }

    @Test
    void isEpicStatusChanges() {
        Epic epic = new Epic("Эпик", "");
        manager.createEpic(epic);
        int epicId = epic.getId();
        Subtask subtask1 = new Subtask("Подзадача 1", "", epicId);
        Subtask subtask2 = new Subtask("Подзадача 2", "", epicId);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        assertEquals(TaskStatuses.NEW, epic.getStatus(), "Неверный статус при создании подзадач");

        subtask1.setStatus(TaskStatuses.IN_PROGRESS);
        manager.updateSubtask(subtask1);
        assertEquals(TaskStatuses.IN_PROGRESS, epic.getStatus(), "Неверный статус при смене одной подзадачи" +
                " на IN_PROGRESS");

        subtask1.setStatus(TaskStatuses.DONE);
        manager.updateSubtask(subtask1);
        assertEquals(TaskStatuses.IN_PROGRESS, epic.getStatus(), "Неверный статус при смене одной подзадачи" +
                " на DONE");

        subtask2.setStatus(TaskStatuses.DONE);
        manager.updateSubtask(subtask2);
        assertEquals(TaskStatuses.DONE, epic.getStatus(), "Неверный статус при смене двух подзадач" +
                " на DONE");
    }


}
