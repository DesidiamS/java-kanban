package service;

import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static model.TaskStatuses.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    String filePath;

    public FileBackedTaskManager(HistoryManager historyManager, String filePath) {
        super(historyManager);
        this.filePath = filePath;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager(), file.getPath());
            while ((line = br.readLine()) != null) {
                if (line.contains("TASK")) {
                    manager.createTask(taskFromString(line));
                } else if (line.contains("EPIC")) {
                    manager.createEpic((Epic) taskFromString(line));
                } else if (line.contains("SUBTASK")) {
                    manager.createSubtask((Subtask) taskFromString(line));
                }
            }
            return manager;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки файла");
        }
    }

    private static Task taskFromString(String value) {
        String[] strTask = value.split(",\\s*");
        String type = strTask[1];
        TaskStatuses status = NEW;
        if (strTask[3].equals(IN_PROGRESS.name())) {
            status = IN_PROGRESS;
        } else if (strTask[3].equals(DONE.name())) {
            status = DONE;
        }

        if (type.equals(TaskTypes.TASK.name())) {
            Task task = new Task(strTask[2], strTask[4]);
            if (status != NEW) {
                task.setStatus(status);
            }
            return task;
        } else if (type.equals(TaskTypes.EPIC.name())) {
            Task task = new Epic(strTask[2], strTask[3]);
            if (status != NEW) {
                task.setStatus(status);
            }
            return task;
        } else if (type.equals(TaskTypes.SUBTASK.name())) {
            Task task = new Subtask(strTask[2], strTask[4], Integer.parseInt(strTask[5]));
            if (status != NEW) {
                task.setStatus(status);
            }
            return task;
        } else {
            return null;
        }
    }

    public void save() throws ManagerSaveException {
        try (Writer writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,epic \n"); // заголовки таблицы
            List<Task> taskList = new ArrayList<>();
            taskList.addAll(tasks.values());
            taskList.addAll(epicTasks.values());
            taskList.addAll(subTasks.values());
            for (Task task : taskList) {
                String line = taskToString(task) + "\n";
                writer.append(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла");
        }
    }

    private String taskToString(Task task) {
        String taskType;
        String line = "";
        if (Task.class.equals(task.getClass())) {
            taskType = TaskTypes.TASK.name();
            line = task.getId() + ", " + taskType + ", " + task.getName() + ", " + task.getStatus() + ", " +
                    task.getDescription();
        } else if (Epic.class.equals(task.getClass())) {
            taskType = TaskTypes.EPIC.name();
            line = task.getId() + ", " + taskType + ", " + task.getName() + ", " + task.getStatus() + ", " +
                    task.getDescription();
        } else if (Subtask.class.equals(task.getClass())) {
            taskType = TaskTypes.SUBTASK.name();
            line = task.getId() + ", " + taskType + ", " + task.getName() + ", " + task.getStatus() + ", " +
                    task.getDescription() + ", " + ((Subtask) task).getEpicId();
        }
        return line;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        try {
            save();
        } catch (ManagerSaveException ignored) {
        }
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        try {
            save();
        } catch (ManagerSaveException ignored) {
        }
    }

    @Override
    public void createSubtask(Subtask subTask) {
        super.createSubtask(subTask);
        try {
            save();
        } catch (ManagerSaveException ignored) {
        }
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        try {
            save();
        } catch (ManagerSaveException ignored) {
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        try {
            save();
        } catch (ManagerSaveException ignored) {
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        try {
            save();
        } catch (ManagerSaveException ignored) {
        }
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        try {
            save();
        } catch (ManagerSaveException ignored) {
        }
    }

    @Override
    public void deleteAllEpicTasks() {
        super.deleteAllEpicTasks();
        try {
            save();
        } catch (ManagerSaveException ignored) {
        }
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        try {
            save();
        } catch (ManagerSaveException ignored) {
        }
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        try {
            save();
        } catch (ManagerSaveException ignored) {
        }
    }

    @Override
    public void deleteEpicTaskById(int id) {
        super.deleteEpicTaskById(id);
        try {
            save();
        } catch (ManagerSaveException ignored) {
        }
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        super.deleteSubTaskById(id);
        try {
            save();
        } catch (ManagerSaveException ignored) {
        }
    }

    @Override
    public void deleteSubTaskByEpicId(int epicId) {
        super.deleteSubTaskByEpicId(epicId);
        try {
            save();
        } catch (ManagerSaveException ignored) {
        }
    }
}
