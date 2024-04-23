import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    Long idSequence = 0L;
    HashMap<Long, Task> tasks = new HashMap<>();
    HashMap<Long, EpicTask> epicTasks = new HashMap<>();
    HashMap<Long, SubTask> subTasks = new HashMap<>();


    public void createTask(Task task) {
        idSequence += 1;
        Task newTask = new Task(task.getTaskName(), task.getTaskDescription(), idSequence, task.getTaskStatus());
        tasks.put(idSequence, newTask);
        System.out.println("Задача с id " + idSequence + " создана");
        System.out.println(tasks.get(idSequence));
    }

    public void createTask(EpicTask epicTask) {
        idSequence += 1;
        EpicTask newTask = new EpicTask(epicTask.getTaskName(), epicTask.getTaskDescription(), idSequence, TaskStatuses.NEW);
        epicTasks.put(idSequence, newTask);
        System.out.println("Задача с id " + idSequence + " создана");
        System.out.println(epicTasks.get(idSequence));
    }

    public void createTask(SubTask subTask) {
        if (epicTasks.containsKey(subTask.getParentId())) {
            idSequence += 1;
            SubTask newTask = new SubTask(subTask.getTaskName(), subTask.getTaskDescription(), idSequence,
                    TaskStatuses.NEW, subTask.getParentId());
            subTasks.put(idSequence, newTask);
        }
    }

    public void updateTask(Task task) {
        if(tasks.containsKey(task.getTaskId())) {
            tasks.put(task.getTaskId(), task);
        }
    }

    public void updateTask(EpicTask epicTask) {
        if (epicTasks.containsKey(epicTask.getTaskId())) {
            EpicTask existingTask = epicTasks.get(epicTask.getTaskId());
            existingTask.setTaskName(epicTask.getTaskName());
            existingTask.setTaskDescription(epicTask.getTaskDescription());
        }
    }

    public void updateTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getTaskId())) {
            subTasks.put(subTask.getTaskId(), subTask);
            checkEpicTaskStatus(subTask.getParentId());
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

    public void deleteTaskById(Long id) {
        tasks.remove(id);
    }

    public void deleteEpicTaskById(Long id) {
        epicTasks.remove(id);
        // Если задачи-родителя не останется - все её подзадачи нужно удалить
        if (!getSubTasksByEpicId(id).isEmpty()) {
            deleteSubTaskByEpicId(id);
        }
    }

    public void deleteSubTaskById(Long id) {
        subTasks.remove(id);
    }

    public void deleteSubTaskByEpicId(Long epicId) {
        for (Long key : subTasks.keySet()) {
            for (SubTask value : subTasks.values()) {
                if (value.getParentId().equals(epicId)) {
                    subTasks.remove(key);
                }
            }
        }
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();
        for (Map.Entry<Long, Task> map : tasks.entrySet()) {
            tasksList.add(map.getValue());
        }
        return tasksList;
    }

    public ArrayList<EpicTask> getEpicTasks() {
        ArrayList<EpicTask> epicTasksList = new ArrayList<>();
        for (Map.Entry<Long, EpicTask> map : epicTasks.entrySet()) {
            epicTasksList.add(map.getValue());
        }
        return epicTasksList;
    }

    public ArrayList<SubTask> getSubTasks() {
        ArrayList<SubTask> subTasksList = new ArrayList<>();
        for (Map.Entry<Long, SubTask> map : subTasks.entrySet()) {
            subTasksList.add(map.getValue());
        }
        return subTasksList;
    }

    public ArrayList<SubTask> getSubTasksByEpicId(Long epicTaskId) {
        ArrayList<SubTask> subTasksList = new ArrayList<>();
        for (Map.Entry<Long, SubTask> map : subTasks.entrySet()) {
            if (map.getValue().getParentId().equals(epicTaskId)) {
                subTasksList.add(map.getValue());
            }
        }
        return subTasksList;
    }

    public Task getTaskById(Long id) {
        return tasks.get(id);
    }

    public EpicTask getEpicTaskById(Long id) {
        return epicTasks.get(id);
    }

    public SubTask getSubTaskById(Long id) {
        return subTasks.get(id);
    }

    public void checkEpicTaskStatus(Long epicTaskId) {
        if (epicTasks.containsKey(epicTaskId)) {
            boolean isHasNew = false;
            boolean isHasInProgress = false;
            for (SubTask subTask : getSubTasksByEpicId(epicTaskId)) {
                if (subTask.getTaskStatus().equals(TaskStatuses.NEW)) {
                    isHasNew = true;
                }
                if (subTask.getTaskStatus().equals(TaskStatuses.IN_PROGRESS)) {
                    isHasInProgress = true;
                    epicTasks.get(epicTaskId).setTaskStatus(TaskStatuses.IN_PROGRESS);
                }
                if (subTask.getTaskStatus().equals(TaskStatuses.DONE)) {
                    if (!isHasNew && !isHasInProgress) {
                        epicTasks.get(epicTaskId).setTaskStatus(TaskStatuses.DONE);
                    } else {
                        epicTasks.get(epicTaskId).setTaskStatus(TaskStatuses.IN_PROGRESS);
                    }
                }
            }
        }
    }

}
