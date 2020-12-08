package agents;

import tasks.TaskType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomExpertiseGenerator {
    private static <T extends Enum<?>> T getRandomEnum(Class<T> enumClass) {
        Random random = new Random();
        int index = random.nextInt(enumClass.getEnumConstants().length);
        return enumClass.getEnumConstants()[index];
    }

    public static List<TaskType> getRandomExpertise(int numDevs) {
        ArrayList<TaskType> types = new ArrayList<>();

        for (int i = 0; i < numDevs; i++) {
            types.add(getRandomEnum(TaskType.class));
        }

        return types;
    }
}

