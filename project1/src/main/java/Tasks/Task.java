package Tasks;

public class Task {
    private int duration;
    private int priority;
    private TaskType type;

    public Task(int duration, int priority, TaskType type) {
        this.duration = duration;
        this.priority = priority;
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public int getPriority() {
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
