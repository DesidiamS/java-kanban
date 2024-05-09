package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int HISTORY_CAPACITY = 9;
    List<Task> taskHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (taskHistory.size() > HISTORY_CAPACITY) {
            // по каким-то причинам не работает метод getFirst, хотя JDK стоит версии 22, по этому использую get(0)
            // v2 попытался использовать LinkedList и метод removeFirst() тоже получил ошибку cannot find symbol
            // при компиляциия, хотя во время написания кода никаких ошибок не подсвечивается, оставил обращение по
            // индексу
            taskHistory.remove(0);
        }

        taskHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(taskHistory);
    }
}
