package model;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, int id, TaskStatuses status, int epicId) {
        super(name, description, id, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
