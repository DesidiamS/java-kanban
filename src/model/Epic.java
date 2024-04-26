package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIdList = new ArrayList<>();
    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubtasksIdList() {
        return subtasksIdList;
    }
}
