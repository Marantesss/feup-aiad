package io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tasks.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ResultsWriter {

    private final String outputFilePath;

    private final Map<String, List<Task>> developerTasks;

    private int totalProjectTime;

    public ResultsWriter(String outputFilePath) {
        this.outputFilePath = outputFilePath;
        this.developerTasks = new HashMap<>();
        this.totalProjectTime = 0;
    }

    public void writeOutput() {
        // create GSON object and output data
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, Object> map = new HashMap<>();
        // add developers and according tasks (automagically)
        map.put("developers", this.developerTasks);
        // add total project time
        map.put("projectTime", this.totalProjectTime);

        try {
            // create folder structure if not existent
            File file = new File(this.outputFilePath);
            file.getParentFile().mkdirs();
            // create a writer
            Writer writer = new FileWriter(file);
            // convert map to JSON File
            gson.toJson(map, writer);
            // close the writer
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addTask(String developerName, Task task) {
        // create map entry if developer has no previous tasks
        if (!this.developerTasks.containsKey(developerName)) {
            this.developerTasks.put(developerName, new LinkedList<>());
        }
        // add task to developer task list
        this.developerTasks.get(developerName).add(task);
        this.totalProjectTime = Math.max(this.totalProjectTime, (task.getStartingInstant() + task.getDuration()));
    }
}
