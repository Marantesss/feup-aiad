package io;

import agents.DeveloperAgent;
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

    private final List<DeveloperAgent> developers;

    private final List<Task> tasks;

    public ConfigReader(String configFilePath) throws IOException {
        this.configFilePath = configFilePath;
        // create GSON instance
        Gson gson = new Gson();
        // read file
        Reader reader = new FileReader(this.configFilePath);
        // read file content as Map
        var json = gson.fromJson(reader, Map.class);
        // parse developers and tasks as list of map<string, string>
        // gson already reads with correct class formats
        List<Map<?, ?>> jsonDevelopers = (ArrayList) json.get("developers");
        List<Map<?, ?>> jsonTasks = (ArrayList) json.get("tasks");

        // create developers
        this.developers = new ArrayList<>();
        for (var jsonDev : jsonDevelopers) {
            this.developers.add(new DeveloperAgent(TaskType.valueOf((String) jsonDev.get("aoe"))));
        }

        // create tasks
        this.tasks = new ArrayList<>();
        for (var jsonTask : jsonTasks) {
            Double duration = (Double) jsonTask.get("duration");
            this.tasks.add(new Task(
                duration.intValue(),
                TaskPriority.valueOf((String) jsonTask.get("priority")),
                TaskType.valueOf((String) jsonTask.get("type"))
            ));
        }
        // close reader
        reader.close();
    }

    public List<DeveloperAgent> getDevelopers() {
        return developers;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public String getConfigFilePath() {
        return configFilePath;
    }
}
