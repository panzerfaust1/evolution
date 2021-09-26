package xyz.ev0lve.evolution;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.HashMap;

public class Config {
    private final HashMap<String, Boolean> values = new HashMap<>();
    private static Config instance;

    private Config() {
        values.put("xray.enable", false);
        values.put("xray.rare_only", false);
        values.put("xray.limit_distance", false);

        values.put("trajectory.enable", false);
        values.put("trajectory.bow", false);
        values.put("trajectory.throwable", false);

        load();
    }

    private void load() {
        try {
            var file = new File("evo.json");
            if (!file.exists() || file.isDirectory()) {
                return;
            }

            var parser = new JsonParser();
            var rawJson = parser.parse(new FileReader("evo.json"));
            if (rawJson.isJsonNull()) {
                return;
            }

            var json = (JsonObject)rawJson;
            json.entrySet().forEach((entry) -> {
                values.put(entry.getKey(), entry.getValue().getAsBoolean());
            });
        } catch (IOException e) {
            Evolution.LOGGER.error(e);
        }
    }

    private void save() {
        try {
            var json = new JsonObject();
            values.forEach(json::addProperty);

            var writer = new BufferedWriter(new FileWriter("evo.json"));
            writer.write(json.toString());
            writer.close();
        } catch (IOException e) {
            Evolution.LOGGER.error(e);
        }
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }

        return instance;
    }

    public static boolean get(String key) {
        return getInstance().values.get(key);
    }

    public static void set(String key, boolean value) {
        var inst = getInstance();
        inst.values.put(key, value);
        inst.save();
    }

    public static void toggle(String key) {
        set(key, !get(key));
    }
}
