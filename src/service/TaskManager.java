package service;

import model.*;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void createTask(Task task) throws TaskTimeException;

    void createEpic(Epic epic);

    void createSubtask(Subtask subTask) throws TaskTimeException;

    void updateTask(Task task) throws TaskTimeException;

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subTask) throws TaskTimeException;

    void deleteAllTasks();

    void deleteAllEpicTasks();

    void deleteAllSubTasks();

    void deleteTaskById(int id);

    void deleteEpicTaskById(int id);

    void deleteSubTaskById(Integer id);

    void deleteSubTaskByEpicId(int epicId);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpicTasks();

    ArrayList<Subtask> getSubTasks();

    ArrayList<Subtask> getSubTasksByEpicId(int epicTaskId);

    Task getTaskById(int id);

    Epic getEpicTaskById(int id);

    Subtask getSubTaskById(int id);

    List<Task> getHistory();

    void checkEpicTaskStatus(int epicTaskId);

    void checkTaskTimeOverlap(Task task) throws TaskTimeException;
}
