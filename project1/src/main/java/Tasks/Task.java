package Tasks;

public class Task {
    private int duration;
    private final TaskPriority priority;
    private final TaskType type;

    public Task(int duration, TaskPriority priority, TaskType type) {
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

    public void tick() {
        if (this.duration > 0)
            this.duration--;
    }
}
