package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatuses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    int idSequence = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epicTasks = new HashMap<>();
    HashMap<Integer, Subtask> subTasks = new HashMap<>();


    public void createTask(Task task) {
        idSequence += 1;
        Task newTask = new Task(task.getName(), task.getDescription(), idSequence, task.getStatus());
        tasks.put(idSequence, newTask);
        System.out.println("Задача с id " + idSequence + " создана");
        System.out.println(tasks.get(idSequence));
    }

    public void createTask(Epic epic) {
        idSequence += 1;
        Epic newTask = new Epic(epic.getName(), epic.getDescription(), idSequence, TaskStatuses.NEW);
        epicTasks.put(idSequence, newTask);
        System.out.println("Задача с id " + idSequence + " создана");
        System.out.println(epicTasks.get(idSequence));
    }

    public void createTask(Subtask subTask) {
        if (epicTasks.containsKey(subTask.getEpicId())) {
            idSequence += 1;
            Subtask newTask = new Subtask(subTask.getName(), subTask.getDescription(), idSequence,
                    TaskStatuses.NEW, subTask.getEpicId());
            subTasks.put(idSequence, newTask);
        }
    }

    public void updateTask(Task task) {
        if(tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateTask(Epic epic) {
        if (epicTasks.containsKey(epic.getId())) {
            Epic existingTask = epicTasks.get(epic.getId());
            existingTask.setName(epic.getName());
            existingTask.setDescription(epic.getDescription());
        }
    }

    public void updateTask(Subtask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            checkEpicTaskStatus(subTask.getEpicId());
        }
    }

    public void deleteAllTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
    }

    public void deleteAllEpicTasks() {
        if (!epicTasks.isEmpty()) {
            epicTasks.clear();
            // Так как задач-родителей больше не останется подзадачи тоже нужно удалить
            subTasks.clear();
        }
    }

    public void deleteAllSubTasks() {
        if (!subTasks.isEmpty()) {
            subTasks.clear();
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicTaskById(int id) {
        epicTasks.remove(id);
        // Если задачи-родителя не останется - все её подзадачи нужно удалить
        if (!getSubTasksByEpicId(id).isEmpty()) {
            deleteSubTaskByEpicId(id);
        }
    }

    public void deleteSubTaskById(int id) {
        subTasks.remove(id);
    }

    public void deleteSubTaskByEpicId(int epicId) {
        for (int key : subTasks.keySet()) {
            for (Subtask value : subTasks.values()) {
                if (value.getEpicId() == epicId) {
                    subTasks.remove(key);
                }
            }
        }
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();
        for (Map.Entry<Integer, Task> map : tasks.entrySet()) {
            tasksList.add(map.getValue());
        }
        return tasksList;
    }

    public ArrayList<Epic> getEpicTasks() {
        ArrayList<Epic> epicTasksList = new ArrayList<>();
        for (Map.Entry<Integer, Epic> map : epicTasks.entrySet()) {
            epicTasksList.add(map.getValue());
        }
        return epicTasksList;
    }

    public ArrayList<Subtask> getSubTasks() {
        ArrayList<Subtask> subTasksList = new ArrayList<>();
        for (Map.Entry<Integer, Subtask> map : subTasks.entrySet()) {
            subTasksList.add(map.getValue());
        }
        return subTasksList;
    }

    public ArrayList<Subtask> getSubTasksByEpicId(int epicTaskId) {
        ArrayList<Subtask> subTasksList = new ArrayList<>();
        for (Map.Entry<Integer, Subtask> map : subTasks.entrySet()) {
            if (map.getValue().getEpicId() == epicTaskId) {
                subTasksList.add(map.getValue());
            }
        }
        return subTasksList;
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicTaskById(int id) {
        return epicTasks.get(id);
    }

    public Subtask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public void checkEpicTaskStatus(int epicTaskId) {
        if (epicTasks.containsKey(epicTaskId)) {
            boolean isHasNew = false;
            boolean isHasInProgress = false;
            for (Subtask subTask : getSubTasksByEpicId(epicTaskId)) {
                if (subTask.getStatus().equals(TaskStatuses.NEW)) {
                    isHasNew = true;
                }
                if (subTask.getStatus().equals(TaskStatuses.IN_PROGRESS)) {
                    isHasInProgress = true;
                    epicTasks.get(epicTaskId).setStatus(TaskStatuses.IN_PROGRESS);
                }
                if (subTask.getStatus().equals(TaskStatuses.DONE)) {
                    if (!isHasNew && !isHasInProgress) {
                        epicTasks.get(epicTaskId).setStatus(TaskStatuses.DONE);
                    } else {
                        epicTasks.get(epicTaskId).setStatus(TaskStatuses.IN_PROGRESS);
                    }
                }
            }
        }
    }

}
