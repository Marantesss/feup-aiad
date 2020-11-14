package tasks;

import java.util.Comparator;

/**
 * Comparator to compare tasks so that a task array sorted using this comparator
 * has the highest priority tasks in the beggining
 */
public class TaskPriorityComparator implements Comparator<Task> {
    @Override
    public int compare(Task task, Task t1) {
        return Integer.compare(t1.getPriority().ordinal(),task.getPriority().ordinal());
    }
}
