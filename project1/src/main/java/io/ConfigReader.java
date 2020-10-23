package io;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class ConfigReader {

    public static void getDevelopers() {
        // create GSON instance
        Gson gson = new Gson();
        // read file
        try (Reader reader = new FileReader("project1/src/main/resources/config.test.json")) {
            // cenas
            Map json = gson.fromJson(reader, HashMap.class);
            // cenas
            System.out.println(json.get("developers"));
            System.out.println(json.get("tasks"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
