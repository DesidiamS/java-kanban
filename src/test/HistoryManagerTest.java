package test;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.InMemoryHistoryManager;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    @Test
    void isHistoryAddAndGet() {
        Task task = new Task("Задача", "");
        task.setId(1);
        Epic epic = new Epic("Эпик", "");
        epic.setId(2);
        Subtask subtask = new Subtask("Подзадача", "", 2);
        subtask.setId(3);
        HistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        assertEquals(3, historyManager.getHistory().size());
    }
}