package proposals;

import tasks.Task;

import java.io.Serializable;

public class Proposal implements Serializable {
    private final int timeUntilCompleted;
    private final Task task; //The task this proposal refers to

    public Proposal(int timeUntilCompleted, Task task) {
        this.timeUntilCompleted = timeUntilCompleted;
        this.task = task;
    }

    public int getTimeUntilCompleted() {
        return timeUntilCompleted;
    }

    public Task getTask() {
        return task;
    }

    @Override
    public String toString() {
        return "Proposal{" +
                "timeUntilCompleted=" + timeUntilCompleted + //TODO: add task to string
                '}';
    }
}
