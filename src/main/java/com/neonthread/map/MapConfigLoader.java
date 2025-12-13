package com.neonthread.map;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapConfigLoader {
    private static MapConfig cachedConfig;
    private static long lastModified;
    private static final String CONFIG_FILE = "config/map.json";

    public static MapConfig load() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) return new MapConfig();

        if (cachedConfig != null && file.lastModified() == lastModified) {
            return cachedConfig;
        }

        cachedConfig = parse(file);
        lastModified = file.lastModified();
        return cachedConfig;
    }

    private static MapConfig parse(File file) {
        MapConfig config = new MapConfig();
        StringBuilder json = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return config;
        }

        String content = json.toString();

        // Extract theme
        Pattern themePattern = Pattern.compile("\"theme\"\\s*:\\s*\"([^\"]+)\"");
        Matcher themeMatcher = themePattern.matcher(content);
        if (themeMatcher.find()) {
            config.setTheme(themeMatcher.group(1));
        }

        // Extract locations block
        Pattern locationsPattern = Pattern.compile("\"locations\"\\s*:\\s*\\{(.+?)\\}");
        Matcher locationsMatcher = locationsPattern.matcher(content);
        if (locationsMatcher.find()) {
            String locationsBlock = locationsMatcher.group(1);
            
            // Extract individual locations: "name": { "x": 123, "y": 456 }
            Pattern locPattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\\{\\s*\"x\"\\s*:\\s*(\\d+)\\s*,\\s*\"y\"\\s*:\\s*(\\d+)\\s*\\}");
            Matcher locMatcher = locPattern.matcher(locationsBlock);
            
            while (locMatcher.find()) {
                String name = locMatcher.group(1);
                int x = Integer.parseInt(locMatcher.group(2));
                int y = Integer.parseInt(locMatcher.group(3));
                config.addLocation(name, x, y);
            }
        }

        return config;
    }
}
