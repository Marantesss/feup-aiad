package proposals;

import tasks.Task;

import java.io.Serializable;

public class Proposal implements Serializable {
    private final int timeUntilCompleted;
    private final int numberOfTasks;
    private final Task task; //The task this proposal refers to

    public Proposal(int timeUntilCompleted, int numberOfTasks, Task task) {
        this.timeUntilCompleted = timeUntilCompleted;
        this.numberOfTasks = numberOfTasks;

        this.task = task;
    }

    public int getTimeUntilCompleted() {
        return timeUntilCompleted;
    }

    public int getNumberOfTasks() {
        return numberOfTasks;
    }

    public Task getTask() {
        return task;
    }

    @Override
    public String toString() {
        return "Proposal{" +
                "timeUntilCompleted=" + timeUntilCompleted +
                ", numberOfTasks=" + numberOfTasks +
                '}';
    }
}
