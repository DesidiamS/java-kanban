package model;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private TaskStatuses status = TaskStatuses.NEW;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public TaskStatuses getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatuses status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return name.equals(task.name) && Objects.equals(description, task.description) && status == task.status && id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }
}
