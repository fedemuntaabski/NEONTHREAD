package com.neonthread.map;

import java.util.HashMap;
import java.util.Map;

public class MapConfig {
    private String theme;
    private Map<String, Point> locations;

    public MapConfig() {
        this.locations = new HashMap<>();
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Map<String, Point> getLocations() {
        return locations;
    }

    public void addLocation(String name, int x, int y) {
        locations.put(name, new Point(x, y));
    }

    public static class Point {
        public int x, y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
