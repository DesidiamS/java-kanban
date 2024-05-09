package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatuses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    Integer idSequence = 0;
    Map<Integer, Task> tasks = new HashMap<>();
    Map<Integer, Epic> epicTasks = new HashMap<>();
    Map<Integer, Subtask> subTasks = new HashMap<>();

    HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void createTask(Task task) {
        idSequence += 1;
        task.setId(idSequence);
        tasks.put(idSequence, task);
    }

    @Override
    public void createEpic(Epic epic) {
        idSequence += 1;
        epic.setId(idSequence);
        epicTasks.put(idSequence, epic);
    }

    @Override
    public void createSubtask(Subtask subTask) {
        if (epicTasks.containsKey(subTask.getEpicId())) {
            idSequence += 1;
            subTask.setId(idSequence);
            subTasks.put(idSequence, subTask);
            int epicId = subTask.getEpicId();
            Epic epic = epicTasks.get(epicId);
            epic.getSubtasksIdList().add(idSequence);
        }
    }

    @Override
    public void updateTask(Task task) {
        int taskId = task.getId();
        if (tasks.containsKey(taskId)) {
            tasks.put(taskId, task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        int epicId = epic.getId();
        if (epicTasks.containsKey(epicId)) {
            Epic existingTask = epicTasks.get(epicId);
            existingTask.setName(epic.getName());
            existingTask.setDescription(epic.getDescription());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int subtaskId = subtask.getId();
        int epicId = subtask.getEpicId();
        if (subTasks.containsKey(subtaskId)) {
            subTasks.put(subtaskId, subtask);
            Epic updatedEpic = epicTasks.get(epicId);
            ArrayList<Integer> subtasksListInEpic = updatedEpic.getSubtasksIdList();
            int subtaskIndex = getSubTasks().indexOf(subtask);
            subtasksListInEpic.set(subtaskIndex, subtaskId);
            checkEpicTaskStatus(epicId);
        }
    }

    @Override
    public void deleteAllTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
    }

    @Override
    public void deleteAllEpicTasks() {
        epicTasks.clear();
        // Так как задач-родителей больше не останется подзадачи тоже нужно удалить
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic value : epicTasks.values()) {
            List<Integer> subtaskIds = value.getSubtasksIdList();
            subtaskIds.clear();
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicTaskById(int id) {
        epicTasks.remove(id);
        // Если задачи-родителя не останется - все её подзадачи нужно удалить
        deleteSubTaskByEpicId(id);
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        for (Epic value : epicTasks.values()) {
            List<Integer> subtasksIdList = value.getSubtasksIdList();
            subtasksIdList.remove(id);
        }
        subTasks.remove(id);
    }

    @Override
    public void deleteSubTaskByEpicId(int epicId) {
        for (Subtask value : getSubTasksByEpicId(epicId)) {
            int subtaskId = value.getId();
            subTasks.remove(subtaskId);
        }
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubTasksByEpicId(int epicTaskId) {
        ArrayList<Subtask> subTasksList = new ArrayList<>();
        for (Map.Entry<Integer, Subtask> map : subTasks.entrySet()) {
            Subtask subtask = map.getValue();
            if (subtask.getEpicId() == epicTaskId) {
                subTasksList.add(subtask);
            }
        }
        return subTasksList;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicTaskById(int id) {
        Epic epic = epicTasks.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubTaskById(int id) {
        Subtask subtask = subTasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void checkEpicTaskStatus(int epicTaskId) {
        if (epicTasks.containsKey(epicTaskId)) {
            boolean isHasNew = false;
            boolean isHasInProgress = false;
            for (Subtask subTask : getSubTasksByEpicId(epicTaskId)) {
                TaskStatuses subtaskStatus = subTask.getStatus();
                if (subtaskStatus.equals(TaskStatuses.NEW)) {
                    isHasNew = true;
                }
                if (subtaskStatus.equals(TaskStatuses.IN_PROGRESS)) {
                    isHasInProgress = true;
                    Epic epic = epicTasks.get(epicTaskId);
                    epic.setStatus(TaskStatuses.IN_PROGRESS);
                }
                if (subtaskStatus.equals(TaskStatuses.DONE)) {
                    if (!isHasNew && !isHasInProgress) {
                        Epic epic = epicTasks.get(epicTaskId);
                        epic.setStatus(TaskStatuses.DONE);
                    } else {
                        Epic epic = epicTasks.get(epicTaskId);
                        epic.setStatus(TaskStatuses.IN_PROGRESS);
                    }
                }
                Epic epic = epicTasks.get(epicTaskId);
                TaskStatuses epicStatus = epic.getStatus();
                if (epicStatus.equals(TaskStatuses.DONE) && (isHasInProgress || isHasNew)) {
                    epic.setStatus(TaskStatuses.IN_PROGRESS);
                }
            }
        }
    }


}
