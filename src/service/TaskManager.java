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
        task.setId(idSequence);
        tasks.put(idSequence, task);
    }

    public void createEpic(Epic epic) {
        idSequence += 1;
        epic.setId(idSequence);
        epicTasks.put(idSequence, epic);
    }

    public void createSubtask(Subtask subTask) {
        if (epicTasks.containsKey(subTask.getEpicId())) {
            idSequence += 1;
            subTask.setId(idSequence);
            subTasks.put(idSequence, subTask);
            epicTasks.get(subTask.getEpicId()).getSubtasksIdList().add(idSequence);
        }
    }

    public void updateTask(Task task) {
        if(tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        if (epicTasks.containsKey(epic.getId())) {
            Epic existingTask = epicTasks.get(epic.getId());
            existingTask.setName(epic.getName());
            existingTask.setDescription(epic.getDescription());
        }
    }

    public void updateSubtask(Subtask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            epicTasks.get(subTask.getEpicId()).getSubtasksIdList().set(getSubTasks().indexOf(subTask), subTask.getId());
            checkEpicTaskStatus(subTask.getEpicId());
        }
    }

    public void deleteAllTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
    }

    public void deleteAllEpicTasks() {
        epicTasks.clear();
        // Так как задач-родителей больше не останется подзадачи тоже нужно удалить
        subTasks.clear();
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic value : epicTasks.values()) {
            value.getSubtasksIdList().clear();
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicTaskById(int id) {
        epicTasks.remove(id);
        // Если задачи-родителя не останется - все её подзадачи нужно удалить
        deleteSubTaskByEpicId(id);
    }

    public void deleteSubTaskById(Integer id) {
        for (Epic value : epicTasks.values()) {
            value.getSubtasksIdList().remove(id);
        }
        subTasks.remove(id);
    }

    public void deleteSubTaskByEpicId(int epicId) {
        for (Subtask value : getSubTasksByEpicId(epicId)) {
            subTasks.remove(value.getId());
        }
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    public ArrayList<Subtask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
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
                if (epicTasks.get(epicTaskId).getStatus().equals(TaskStatuses.DONE) && (isHasInProgress || isHasNew)) {
                    epicTasks.get(epicTaskId).setStatus(TaskStatuses.IN_PROGRESS);
                }
            }
        }
    }

}
