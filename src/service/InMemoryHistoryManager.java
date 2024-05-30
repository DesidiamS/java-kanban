package service;
import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    Node<Task> first;
    Node<Task> last;
    Map<Integer, Node<Task>> taskHistory = new HashMap<>();

    static class Node<T> {
        T task;
        Node<T> next;
        Node<T> prev;

        public Node(T task, Node<T> next, Node<T> prev) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }

    @Override
    public void add(Task task) {
        linkLast(task);
        if (taskHistory.containsKey(task.getId())) {
            taskHistory.remove(task.getId());
        }
        taskHistory.put(task.getId(), new Node<>(task, first, last));
    }

    void linkLast(Task task) {
        if (first == null) {
            first = new Node<>(task, null, null);
        } else {
            Node<Task> node = first;
            while (node.next != null) {
                node = node.next;
            }
            last = new Node<>(task, null, node.prev);
        }
    }

    void removeNode(Node<Task> node) {
        if (first == null) {
            return;
        }
        if (first.task.equals(node.task)) {
            first = first.next;
            return;
        }
        Node<Task> currentNode = first;
        while (currentNode.next != null && !currentNode.next.task.equals(node.task)) {
            currentNode = currentNode.next;
        }
        if (currentNode.next != null) {
            currentNode.next = currentNode.next.next;
        }
    }

    @Override
    public void remove(int id) {
        removeNode(taskHistory.get(id));
        taskHistory.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> taskHistoryList = new ArrayList<>();
        for (Integer key : taskHistory.keySet()) {
            taskHistoryList.add(taskHistory.get(key).task);
        }
        return taskHistoryList;
    }
}
