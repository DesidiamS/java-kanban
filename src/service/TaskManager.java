package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subTask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subTask);

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
}
