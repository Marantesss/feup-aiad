package Agents;

import Tasks.Task;
import Tasks.TaskType;
import jade.core.Agent;

import java.util.HashMap;

public class DeveloperAgent extends Agent {
    private static int count = 0;

    private final int id;
    private final TaskType aoe;
    private final HashMap<Task,Integer> tasks; //key -> task; Value: -> instant a task is finished
    private Task latestTask; //The latest task this developer is working on

    //TODO: Add aoe
    //TODO: Comment functions
    //TODO: Create proposal

    public DeveloperAgent(TaskType aoe) {
        this.id = ++count;
        this.aoe = aoe;
        this.tasks = new HashMap<>();
    }

    /**
     * Returns the minimum instant a task can be allocated to
     * @param task The task to allocate
     * @return The minimum instant this developer can start the task
     */
    private int allocateTask(Task task) {
        if (latestTask == null) {
            return 0;
        }

        //Get the earliest theoretical instant the task can be finished
        int earlyInstant = tasks.get(latestTask) + latestTask.getDuration();

        return Math.max(earlyInstant, task.getStartingInstant());
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

    /**
     * Returns the task Hashmap of the developer
     * @return
     */
    public HashMap<Task, Integer> getTasks() {
        return  this.tasks;
    }

    public void addTask(Task task) {
        //Get instant the task can be started
        int instant = allocateTask(task);

        tasks.put(task,instant);
    }

    public TaskType getAoe() {
        return aoe;
    }

    @Override
    public String toString() {
        return "DeveloperAgent{" +
                "id=" + id +
                ", tasks=" + tasks + //TODO: print tasks instead of pointer
                ", aoe=" + aoe +
                '}';
    }
}
