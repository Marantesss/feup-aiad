package tasks;

import java.util.LinkedList;
import java.util.Random;

public class RandomTaskGenerator {
    private final int maxTaskDuration; //Specifies the maximum duration a single task can have
    private final int minStartingInstant; //Specifies the minimum value of the starting instant of a task
    private final int maxStaringTime; //Specifies the maximum value of the starting instant of a task

    private static <T extends Enum<?>> T getRandomEnum(Class<T> enumClass) {
        Random random = new Random();
        int index = random.nextInt(enumClass.getEnumConstants().length);
        return enumClass.getEnumConstants()[index];
    }

    private static int randomInRange(int min, int max){
        if (min > max) {
            throw new IllegalArgumentException("Maximum value must be greater than the minimum");
        }

        Random random = new Random();
        return random.nextInt((max - min) + 1 ) + min;
    }

    public RandomTaskGenerator(int maxTaskDuration, int minStartingInstant, int maxStaringTime) {
        if (minStartingInstant < 0) {
            throw new IllegalArgumentException("Minimum starting time must be greater than 0");
        }

        this.maxTaskDuration = maxTaskDuration;
        this.minStartingInstant = minStartingInstant;
        this.maxStaringTime = maxStaringTime;
    }

    public Task generateTask() {
        TaskType type = getRandomEnum(TaskType.class);
        TaskPriority priority = getRandomEnum(TaskPriority.class);

        Random random = new Random();

        int startingTime = randomInRange(this.minStartingInstant,this.maxStaringTime);
        int duration = random.nextInt(this.maxTaskDuration) + 1 ;

        return new Task(startingTime,duration,priority,type);
    }

    public LinkedList<Task> generateTaskList(int numTasks) {
        if (numTasks <= 0) {
            throw new IllegalArgumentException("The number of tasks to generate must be greater than 0");
        }

        LinkedList<Task> tasks = new LinkedList<>();

        for (int i = 0; i < numTasks; i++) {
            tasks.add(generateTask());
        }

        return tasks;
    }
}
