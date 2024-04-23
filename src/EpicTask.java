public class EpicTask extends Task {
    public EpicTask(String taskName, String taskDescription) {
        super(taskName, taskDescription);
    }

    public EpicTask(String taskName, String taskDescription, Long taskId, TaskStatuses taskStatus) {
        super(taskName, taskDescription, taskId, taskStatus);
    }
}
