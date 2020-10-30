package Agents;

import Tasks.Task;
import Tasks.TaskType;
import jade.core.Agent;

public class DeveloperAgent extends Agent {
    private static int count = 0;

    private final int id;
    private Task task;
    private final TaskType aoe;

    public DeveloperAgent(TaskType aoe) {
        this.id = ++count;
        this.aoe = aoe;
    }

    @Override
    protected void setup() {
        super.setup();
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }

    public int getId() {
        return id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public TaskType getAoe() {
        return aoe;
    }

    @Override
    public String toString() {
        return "DeveloperAgent{" +
                "id=" + id +
                ", task=" + task +
                ", aoe=" + aoe +
                '}';
    }
}
