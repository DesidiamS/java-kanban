import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;
import static service.Managers.getDefault;

public abstract class TaskManagersTest<T extends TaskManager> {

    T manager;

    @BeforeAll
    static void isTaskManagerInitiated() {
        assertNotNull(getDefault());
    }

    @BeforeAll
    static void isHistoryManagerInitiated() {
        assertNotNull(Managers.getDefaultHistory());
    }

    abstract T getManager();

    @BeforeEach
    void createTaskManager() {
        manager = getManager();
    }

    @Test
    void isTaskCreated() {
        Task task = new Task("Задача", "");
        try {
            manager.createTask(task);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
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
        try {
            manager.createSubtask(subtask);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
        assertTrue(manager.getSubTasks().size() > 0);
    }

    @Test
    void isTaskUpdated() {
        Task task = new Task("Задача", "");
        String expectedName = "Задача 1";
        try {
            manager.createTask(task);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
        // Постарался максимально разделить все вызовы методов подряд через точку. Наплодилось кучу переменных,
        // но надеюсь, так лучше
        int actualId = task.getId();
        Task updatedTask = new Task(expectedName, "");
        updatedTask.setId(actualId);
        try {
            manager.updateTask(updatedTask);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
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
        try {
            manager.createSubtask(subtask);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
        int actualId = subtask.getId();
        Subtask updatedSubtask = new Subtask(expectedName, "", epic.getId());
        updatedSubtask.setId(actualId);
        try {
            manager.updateSubtask(updatedSubtask);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
        Subtask actualSubtask = manager.getSubTaskById(actualId);
        String actualName = actualSubtask.getName();
        assertEquals(expectedName, actualName);
    }

    @Test
    void isSubtaskDoNotSelfAddedInEpic() {
        Epic epic = new Epic("Эпик", "");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "", 2); // 2 - id этой же подзадачи
        try {
            manager.createSubtask(subtask);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
        assertNull(manager.getSubTaskById(2));
    }

    @Test
    void checkSubtasksInEpic() {
        Epic epic = new Epic("Тестовый эпик", "");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "", epic.getId());
        try {
            manager.createSubtask(subtask);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
        assertEquals(manager.getSubTasksByEpicId(epic.getId()), manager.getSubTasks());
    }

    @Test
    void isIdNotConflict() {
        Task task = new Task("Задача", "");
        Task task1 = new Task("Задача 1", "");
        try {
            manager.createTask(task);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
        task1.setId(task.getId());
        try {
            manager.createTask(task1);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
        assertEquals(2, manager.getTasks().size());
    }

    @Test
    void isTaskFindById() {
        Task task = new Task("Задача", "");
        try {
            manager.createTask(task);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
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
        try {
            manager.createSubtask(subtask);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
        int subtaskId = subtask.getId();
        assertEquals(subtask, manager.getSubTaskById(subtaskId));
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
    void isEpicStatusChanges() {
        Epic epic = new Epic("Эпик", "");
        manager.createEpic(epic);
        int epicId = epic.getId();
        Subtask subtask1 = new Subtask("Подзадача 1", "", epicId);
        Subtask subtask2 = new Subtask("Подзадача 2", "", epicId);
        try {
            manager.createSubtask(subtask1);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
        try {
            manager.createSubtask(subtask2);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
        assertEquals(TaskStatuses.NEW, epic.getStatus(), "Неверный статус при создании подзадач");

        subtask1.setStatus(TaskStatuses.IN_PROGRESS);
        try {
            manager.updateSubtask(subtask1);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
        assertEquals(TaskStatuses.IN_PROGRESS, epic.getStatus(), "Неверный статус при смене одной подзадачи" +
                " на IN_PROGRESS");

        subtask1.setStatus(TaskStatuses.DONE);
        try {
            manager.updateSubtask(subtask1);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
        assertEquals(TaskStatuses.IN_PROGRESS, epic.getStatus(), "Неверный статус при смене одной подзадачи" +
                " на DONE");

        subtask2.setStatus(TaskStatuses.DONE);
        try {
            manager.updateSubtask(subtask2);
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
        assertEquals(TaskStatuses.DONE, epic.getStatus(), "Неверный статус при смене двух подзадач" +
                " на DONE");
    }
}
