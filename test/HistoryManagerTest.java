import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.InMemoryHistoryManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HistoryManagerTest {

    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void taskCreate() {
        this.task = new Task("Задача", "");
        task.setId(1);
        this.epic = new Epic("Эпик", "");
        epic.setId(2);
        this.subtask = new Subtask("Подзадача", "", 2);
        subtask.setId(3);
    }

    @Test
    void isHistoryAddAndGet() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        assertEquals(3, historyManager.getHistory().size());
    }

    @Test
    void isHistoryNotDouble() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.add(subtask);
        assertEquals(3, historyManager.getHistory().size());
    }

    @Test
    void isTaskRemoved() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.remove(task.getId());
        assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    void isHistoryEmpty() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        assertTrue(historyManager.getHistory().isEmpty());
    }
}