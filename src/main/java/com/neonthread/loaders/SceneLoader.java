package com.neonthread.loaders;

import com.neonthread.NarrativeScene;
import com.neonthread.NarrativeScene.SceneOption;
import com.neonthread.NarrativeScene.AttributeCheck;
import com.neonthread.NarrativeScene.Consequence;
import com.neonthread.stats.StatType;
import com.neonthread.utils.SimpleJsonParser;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneLoader {
    private static final String SCENES_FILE = "config/scenes.json";
    private static Map<String, NarrativeScene> sceneCache = new HashMap<>();

    public static void loadScenes() {
        File file = new File(SCENES_FILE);
        if (!file.exists()) {
            System.err.println("Scenes file not found: " + SCENES_FILE);
            return;
        }

        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            Object parsed = SimpleJsonParser.parse(content);
            
            if (parsed instanceof List) {
                List<Object> list = (List<Object>) parsed;
                for (Object item : list) {
                    if (item instanceof Map) {
                        try {
                            NarrativeScene scene = parseScene((Map<String, Object>) item);
                            sceneCache.put(scene.getId(), scene);
                        } catch (Exception e) {
                            System.err.println("Error parsing scene: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static NarrativeScene getScene(String id) {
        if (sceneCache.isEmpty()) {
            loadScenes();
        }
        return sceneCache.get(id);
    }

    private static NarrativeScene parseScene(Map<String, Object> data) {
        String id = (String) data.get("id");
        String title = (String) data.get("title");
        String text = (String) data.get("text");
        
        NarrativeScene scene = new NarrativeScene(id, title, text);
        
        if (data.containsKey("location")) scene.setUbicacion((String) data.get("location"));
        if (data.containsKey("music")) scene.setMusicaOpcional((String) data.get("music"));
        if (data.containsKey("isEnd")) scene.setEsCierre((Boolean) data.get("isEnd"));

        if (data.containsKey("options")) {
            List<Object> opts = (List<Object>) data.get("options");
            for (Object o : opts) {
                scene.addOpcion(parseOption((Map<String, Object>) o));
            }
        }

        return scene;
    }

    private static SceneOption parseOption(Map<String, Object> data) {
        String text = (String) data.get("text");
        String nextSceneId = (String) data.get("nextSceneId");
        
        SceneOption option = new SceneOption(text, nextSceneId);
        
        if (data.containsKey("checks")) {
            List<Object> checks = (List<Object>) data.get("checks");
            for (Object c : checks) {
                Map<String, Object> checkData = (Map<String, Object>) c;
                StatType type = StatType.valueOf((String) checkData.get("type"));
                int min = ((Number) checkData.get("min")).intValue();
                String desc = (String) checkData.get("desc");
                option.addCheck(new AttributeCheck(type, min, desc));
            }
        }

        if (data.containsKey("consequences")) {
            List<Object> cons = (List<Object>) data.get("consequences");
            for (Object c : cons) {
                Map<String, Object> conData = (Map<String, Object>) c;
                Consequence.ConsequenceType type = Consequence.ConsequenceType.valueOf((String) conData.get("type"));
                String key = (String) conData.get("key");
                int val = conData.containsKey("value") ? ((Number) conData.get("value")).intValue() : 0;
                option.addConsecuencia(new Consequence(type, key, val));
            }
        }
        
        if (data.containsKey("requiredFlags")) {
             Map<String, Object> flags = (Map<String, Object>) data.get("requiredFlags");
             for (Map.Entry<String, Object> entry : flags.entrySet()) {
                 option.addFlagRequerido(entry.getKey(), (Boolean) entry.getValue());
             }
        }

        return option;
    }
}
