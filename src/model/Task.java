package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private TaskStatuses status = TaskStatuses.NEW;

    private Duration duration;
    private LocalDateTime startTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatuses getStatus() {
        return status;
    }

    public void setStatus(TaskStatuses status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plus(duration);
        } else {
            return null;
        }
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
