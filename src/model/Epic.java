package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIdList = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubtasksIdList() {
        return subtasksIdList;
    }

    public void setSubtasksIdList(ArrayList<Integer> subtasksIdList) {
        this.subtasksIdList = subtasksIdList;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
