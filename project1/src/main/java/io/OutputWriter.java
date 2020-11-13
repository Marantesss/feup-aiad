package io;

import agents.DeveloperAgent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutputWriter {

    private final String outputFilePath;

    private List<DeveloperAgent> developers;

    private int totalProjectTime;

    public OutputWriter(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public void writeOutput() {
        // calculate total project time
        this.calculateTotalProjectTime();
        // create GSON object and output data
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, Object> map = new HashMap<>();
        // add total project time
        map.put("projectTime", this.totalProjectTime);
        // add developers and according tasks (automagically)
        map.put("developers", this.developers);

        try {
            // create a writer
            Writer writer = new FileWriter(this.outputFilePath);
            // convert map to JSON File
            gson.toJson(map, writer);
            // close the writer
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void calculateTotalProjectTime() {
        // start by resetting total time until completed
        this.totalProjectTime = 0;
        // loop trough all developers and find the largest completion instant
        for(var developer : this.developers) {
            // get developers last task completion instant
            int lastTaskCompletionInstant = developer.getTaskCompletionInstant(developer.getLatestTask());
            // update total time until completed
            this.totalProjectTime = Math.max(lastTaskCompletionInstant, this.totalProjectTime);
        }
    }


}
