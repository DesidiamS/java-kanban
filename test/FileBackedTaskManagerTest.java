import model.ManagerSaveException;
import model.Task;
import model.TaskTimeException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;
import service.InMemoryHistoryManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagersTest {

    static Path file;
    FileBackedTaskManager fileManager;

    @BeforeAll
    static void createPath() {
        try {
            file = Files.createTempFile("test", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void createFileBackedManager() {
        fileManager = getManager();
    }

    @Override
    public FileBackedTaskManager getManager() {
        return new FileBackedTaskManager(new InMemoryHistoryManager(), file.getFileName().toString());
    }

    @Test
    void isFileCreated() {
        try {
            manager.createTask(new Task("Тест", "Описание"));
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
        try {
            fileManager.save();
        } catch (ManagerSaveException e) {
            assertNull(e.getMessage());
        }
    }

    @Test
    void isFileLoaded() {
        try {
            manager.createTask(new Task("Тест", "Описание"));
        } catch (TaskTimeException e) {
            throw new RuntimeException(e);
        }
        try {
            fileManager.save();
        } catch (ManagerSaveException e) {
            new RuntimeException(e);
        }
        try {
            assertNotNull(FileBackedTaskManager.loadFromFile(file.toFile()));
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }
}
