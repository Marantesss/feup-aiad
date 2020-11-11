package tasks;

import java.io.Serializable;

public class Task implements Serializable {
    private static int count = 0;

    private final int id;
    private int duration;
    private final TaskPriority priority;
    private final TaskType type;

    public Task(int duration, TaskPriority priority, TaskType type) {
        this.id = ++count;
        this.duration = duration;
        this.priority = priority;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", duration=" + duration +
                ", priority=" + priority +
                ", type=" + type +
                '}';
    }
}
