package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int HISTORY_CAPACITY = 10;
    List<Task> taskHistory = new ArrayList<>(HISTORY_CAPACITY);

    @Override
    public void add(Task task) {
        if (taskHistory.size() > HISTORY_CAPACITY - 1) {
            // по каким-то причинам не работает метод getFirst, хотя JDK стоит версии 22, по этому использую get(0)
            taskHistory.remove(taskHistory.get(0));
        }

        taskHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return taskHistory;
    }
}
