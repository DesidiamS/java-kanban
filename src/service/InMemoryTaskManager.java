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
    int idSequence = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epicTasks = new HashMap<>();
    HashMap<Integer, Subtask> subTasks = new HashMap<>();

    HistoryManager historyManager = Managers.getDefaultHistory();

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
            epicTasks.get(subTask.getEpicId()).getSubtasksIdList().add(idSequence);
        }
    }

    @Override
    public void updateTask(Task task) {
        if(tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicTasks.containsKey(epic.getId())) {
            Epic existingTask = epicTasks.get(epic.getId());
            existingTask.setName(epic.getName());
            existingTask.setDescription(epic.getDescription());
        }
    }

    @Override
    public void updateSubtask(Subtask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            epicTasks.get(subTask.getEpicId()).getSubtasksIdList().set(getSubTasks().indexOf(subTask), subTask.getId());
            checkEpicTaskStatus(subTask.getEpicId());
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
            value.getSubtasksIdList().clear();
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
            value.getSubtasksIdList().remove(id);
        }
        subTasks.remove(id);
    }

    @Override
    public void deleteSubTaskByEpicId(int epicId) {
        for (Subtask value : getSubTasksByEpicId(epicId)) {
            subTasks.remove(value.getId());
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
            if (map.getValue().getEpicId() == epicTaskId) {
                subTasksList.add(map.getValue());
            }
        }
        return subTasksList;
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicTaskById(int id) {
        historyManager.add(epicTasks.get(id));
        return epicTasks.get(id);
    }

    @Override
    public Subtask getSubTaskById(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
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
