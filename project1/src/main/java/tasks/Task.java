package tasks;

import java.io.Serializable;

public class Task implements Serializable {
    private static int count = 0;

    private final int id;
    private int duration;
    private final int startingInstant;
    private final TaskPriority priority;
    private final TaskType type;

    public Task(int startingInstant,int duration, TaskPriority priority, TaskType type) {
        this.id = ++count;
        this.startingInstant = startingInstant;
        this.duration = duration;
        this.priority = priority;
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public TaskType getType() {
        return type;
    }

    /* TODO: commented for now, but will be removed later
    public void tick() {
        if (this.duration > 0)
            this.duration--;
    }
     */

    public int getId() {
        return id;
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
