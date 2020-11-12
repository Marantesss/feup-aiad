package io;

import agents.strategies.ChooseDeveloperLowestTimeStrategy;
import agents.strategies.ChooseDeveloperRandomStrategy;
import agents.strategies.ChooseDeveloperStrategy;
import tasks.RandomTaskGenerator;
import tasks.Task;
import tasks.TaskPriority;
import tasks.TaskType;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigReader {

    private final String configFilePath;

    private final ChooseDeveloperStrategy strategy;

    private final List<TaskType> developersExpertise;

    private final List<Task> tasks;

    public ConfigReader(String configFilePath) throws IOException {
        this.configFilePath = configFilePath;
        // create GSON instance
        Gson gson = new Gson();
        // read file
        Reader reader = new FileReader(this.configFilePath);
        // read file content as Map
        var json = gson.fromJson(reader, Map.class);
        // parse scrum master strategy
        this.strategy = this.pickStrategy((String) json.get("strategy"));
        // parse developers and tasks as list of map<string, string>
        // gson already reads with correct class formats
        List<Map<?, ?>> jsonDevelopers = (ArrayList) json.get("developers");
        List<Map<?, ?>> jsonTasks = (ArrayList) json.get("tasks");

        // create developers
        this.developersExpertise = new ArrayList<>();
        for (var jsonDev : jsonDevelopers) {
            this.developersExpertise.add(TaskType.valueOf((String) jsonDev.get("aoe")));
        }

        // create tasks
        // tasks might not be present, and if such then they are random
        if (jsonTasks == null) {
            var taskGenerator = new RandomTaskGenerator(10, 0, 30);
            this.tasks = taskGenerator.generateTaskList(20);
        } else {
            this.tasks = new ArrayList<>();
            for (var jsonTask : jsonTasks) {
                Double duration = (double) jsonTask.get("duration"); // TODO: check this out (not working with simao)
                this.tasks.add(new Task(0, //TODO: parse staring instant
                        duration.intValue(),
                        TaskPriority.valueOf((String) jsonTask.get("priority")),
                        TaskType.valueOf((String) jsonTask.get("type"))
                ));
            }
        }
        // close reader
        reader.close();
    }

    private ChooseDeveloperStrategy pickStrategy(String strategyName) {
        switch (strategyName.toLowerCase()) {
            case "random":
                return new ChooseDeveloperRandomStrategy();
            case "lowesttime":
                return new ChooseDeveloperLowestTimeStrategy();
            default:
                break;
        }
        return null;
    }

    public List<TaskType> getDevelopersExpertise() {
        return developersExpertise;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public String getConfigFilePath() {
        return configFilePath;
    }

    public ChooseDeveloperStrategy getStrategy() {
        return strategy;
    }
}
