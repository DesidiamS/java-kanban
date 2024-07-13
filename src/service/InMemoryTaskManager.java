package service;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    Integer idSequence = 0;
    Map<Integer, Task> tasks = new HashMap<>();
    Map<Integer, Epic> epicTasks = new HashMap<>();
    Map<Integer, Subtask> subTasks = new HashMap<>();

    HistoryManager historyManager;

    // Если у задачи нет времени чтобы она добавлялась в конец
    TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(
            task -> Optional.ofNullable(task.getStartTime()).orElse(LocalDateTime.MAX)));

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public void createTask(Task task) throws TaskTimeException {
        idSequence += 1;
        task.setId(idSequence);
        checkTaskTimeOverlap(task);
        tasks.put(idSequence, task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        idSequence += 1;
        epic.setId(idSequence);
        epicTasks.put(idSequence, epic);
    }

    @Override
    public void createSubtask(Subtask subTask) throws TaskTimeException {
        if (epicTasks.containsKey(subTask.getEpicId())) {
            idSequence += 1;
            subTask.setId(idSequence);
            checkTaskTimeOverlap(subTask);
            subTasks.put(idSequence, subTask);
            int epicId = subTask.getEpicId();
            Epic epic = epicTasks.get(epicId);
            checkEpicTime(epicId);
            try {
                epic.getSubtasksIdList().add(idSequence);
            } catch (NullPointerException e) {
                epic.setSubtasksIdList(new ArrayList<>(idSequence));
            }
            prioritizedTasks.add(subTask);
        }
    }

    @Override
    public void updateTask(Task task) throws TaskTimeException {
        int taskId = task.getId();
        Task existingTask = tasks.get(taskId);
        if (tasks.containsKey(taskId)) {
            checkTaskTimeOverlap(task);
            tasks.put(taskId, task);
            prioritizedTasks.remove(existingTask);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        int epicId = epic.getId();
        Epic existingEpic = epicTasks.get(epicId);
        if (epicTasks.containsKey(epicId)) {
            Epic existingTask = epicTasks.get(epicId);
            existingTask.setName(epic.getName());
            existingTask.setDescription(epic.getDescription());
            prioritizedTasks.remove(existingEpic);
            prioritizedTasks.add(epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) throws TaskTimeException {
        int subtaskId = subtask.getId();
        int epicId = subtask.getEpicId();
        Subtask existingSubtask = subTasks.get(subtaskId);
        if (subTasks.containsKey(subtaskId)) {
            checkTaskTimeOverlap(subtask);
            subTasks.put(subtaskId, subtask);
            Epic updatedEpic = epicTasks.get(epicId);
            ArrayList<Integer> subtasksListInEpic = updatedEpic.getSubtasksIdList();
            int subtaskIndex = getSubTasks().indexOf(subtask);
            subtasksListInEpic.set(subtaskIndex, subtaskId);
            checkEpicTaskStatus(epicId);
            checkEpicTime(epicId);
            prioritizedTasks.remove(existingSubtask);
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public void deleteAllTasks() {
        if (!tasks.isEmpty()) {
            prioritizedTasks.removeAll(tasks.values());
            tasks.clear();
        }
    }

    @Override
    public void deleteAllEpicTasks() {
        epicTasks.clear();
        prioritizedTasks.removeAll(epicTasks.values());
        // Так как задач-родителей больше не останется подзадачи тоже нужно удалить
        subTasks.clear();
        prioritizedTasks.removeAll(subTasks.values());
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic value : epicTasks.values()) {
            List<Integer> subtaskIds = value.getSubtasksIdList();
            subtaskIds.clear();
            prioritizedTasks.removeAll(subTasks.values());
        }
    }

    @Override
    public void deleteTaskById(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
    }

    @Override
    public void deleteEpicTaskById(int id) {
        prioritizedTasks.remove(epicTasks.get(id));
        epicTasks.remove(id);
        // Если задачи-родителя не останется - все её подзадачи нужно удалить
        try {
            deleteSubTaskByEpicId(id);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        for (Epic value : epicTasks.values()) {
            List<Integer> subtasksIdList = value.getSubtasksIdList();
            subtasksIdList.remove(id);
        }
        prioritizedTasks.remove(subTasks.get(id));
        subTasks.remove(id);
        checkEpicTime(id);
    }

    @Override
    public void deleteSubTaskByEpicId(int epicId) {
        for (Subtask value : getSubTasksByEpicId(epicId)) {
            int subtaskId = value.getId();
            prioritizedTasks.remove(subTasks.get(subtaskId));
            subTasks.remove(subtaskId);
        }
        checkEpicTime(epicId);
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
        return (ArrayList<Subtask>) subTasks.entrySet().stream()
                .filter(entry -> entry.getValue().getEpicId() == epicTaskId)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
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
                    Epic epic = epicTasks.get(epicTaskId);
                    if (!isHasNew && !isHasInProgress) {
                        epic.setStatus(TaskStatuses.DONE);
                    } else {
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

    public void checkEpicTime(Integer id) {
        Epic epic = epicTasks.get(id);
        if (epic != null) {
            List<Subtask> subTasks = getSubTasksByEpicId(id);
            if (!subTasks.isEmpty()) {
                epic.setStartTime(
                        subTasks.stream()
                                .map(Task::getStartTime)
                                .filter(Objects::nonNull)
                                .min(LocalDateTime::compareTo)
                                .orElse(null)
                );
                subTasks.stream()
                        .filter(subtask -> subtask != null && subtask.getDuration() != null)
                        .map(Subtask::getDuration)
                        .reduce(Duration::plus)
                        .ifPresent(epic::setDuration);
                epic.setStartTime(
                        subTasks.stream()
                                .filter(subtask -> subtask != null && subtask.getStartTime() != null)
                                .map(Subtask::getStartTime)
                                .max(LocalDateTime::compareTo)
                                .orElse(null)
                );
            } else { // На случай, если удалили последнюю подзадачу
                epic.setStartTime(null);
                epic.setEndTime(null);
                epic.setDuration(null);
            }
        }
    }

    @Override
    // В задаче сказано сделать чтобы метод возвращал boolean, но вместо того, чтобы писать вызов ошибки при каждом
    // создании задач, лучше вызвать Exception в этом методе. Если нет, то переделаю
    public void checkTaskTimeOverlap(Task newTask) throws TaskTimeException {
        for (Task task : prioritizedTasks) {
            if (task.getId() == newTask.getId()) {
                continue;
            }
            try {
                if (newTask.getStartTime().isBefore(task.getEndTime()) &&
                        newTask.getEndTime().isAfter(task.getStartTime())) {
                    throw new TaskTimeException("Пересечение задач по времени!");
                }
            } catch (NullPointerException ignored) {
                // Если у задачи нет времени, то не выбрасывать исключение
            }
        }
    }
}
