package model;

public class Epic extends Task {
    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id, TaskStatuses status) {
        super(name, description, id, status);
    }
}
