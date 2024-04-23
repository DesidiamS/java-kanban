public class SubTask extends Task {
    private final Long parentId;

    public SubTask(String taskName, String taskDescription, Long taskId, TaskStatuses taskStatus, Long parentId) {
        super(taskName, taskDescription, taskId, taskStatus);
        this.parentId = parentId;
    }

    public Long getParentId() {
        return parentId;
    }
}
