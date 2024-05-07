package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public class Managers {
    public TaskManager getDefault() {
        return new TaskManager() {
            @Override
            public void createTask(Task task) {

            }

            @Override
            public void createEpic(Epic epic) {

            }

            @Override
            public void createSubtask(Subtask subTask) {

            }

            @Override
            public void updateTask(Task task) {

            }

            @Override
            public void updateEpic(Epic epic) {

            }

            @Override
            public void updateSubtask(Subtask subTask) {

            }

            @Override
            public void deleteAllTasks() {

            }

            @Override
            public void deleteAllEpicTasks() {

            }

            @Override
            public void deleteAllSubTasks() {

            }

            @Override
            public void deleteTaskById(int id) {

            }

            @Override
            public void deleteEpicTaskById(int id) {

            }

            @Override
            public void deleteSubTaskById(Integer id) {

            }

            @Override
            public void deleteSubTaskByEpicId(int epicId) {

            }

            @Override
            public ArrayList<Task> getTasks() {
                return null;
            }

            @Override
            public ArrayList<Epic> getEpicTasks() {
                return null;
            }

            @Override
            public ArrayList<Subtask> getSubTasks() {
                return null;
            }

            @Override
            public ArrayList<Subtask> getSubTasksByEpicId(int epicTaskId) {
                return null;
            }

            @Override
            public Task getTaskById(int id) {
                return null;
            }

            @Override
            public Epic getEpicTaskById(int id) {
                return null;
            }

            @Override
            public Subtask getSubTaskById(int id) {
                return null;
            }

            @Override
            public List<Task> getHistory() {
                return null;
            }

            @Override
            public void checkEpicTaskStatus(int epicTaskId) {

            }
        };
    }

    static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
