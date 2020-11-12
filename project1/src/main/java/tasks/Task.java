package tasks;

import java.io.Serializable;

public class Task implements Serializable {
    private static int count = 0;

    private final int id;
    private int duration;
    private final int startingInstant;
    private final TaskPriority priority;
    private final TaskType type;

    public Task(int startingInstant, int duration, TaskPriority priority, TaskType type) {
        this.id = ++count;
        this.startingInstant = startingInstant;
        this.duration = duration;
        this.priority = priority;
        this.type = type;
    }

    // Utilizado no scrum master temp
    public Task(int duration, TaskPriority priority, TaskType type) {
        this.id = ++count;
        this.duration = duration;
        this.priority = priority;
        this.type = type;
        this.startingInstant = 0;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", startingInstant=" + startingInstant +
                ", duration=" + duration +
                ", priority=" + priority +
                ", type=" + type +
                '}';
    }

    public int getStartingInstant() {
        return startingInstant;
    }
}
