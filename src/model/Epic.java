package model;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public Epic(String name, String description, int id) {
        super(name, description, id);
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(HashMap<Integer, Subtask> subtasks) {
        this.subtasks = subtasks;
    }
}
