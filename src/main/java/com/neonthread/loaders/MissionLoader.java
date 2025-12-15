package com.neonthread.loaders;

import com.neonthread.Mission;
import com.neonthread.MissionBuilder;
import com.neonthread.utils.SimpleJsonParser;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MissionLoader {
    private static final String MISSIONS_FILE = "config/missions.json";
    @SuppressWarnings("unchecked")    public static List<Mission> loadMissions() {
        List<Mission> missions = new ArrayList<>();
        File file = new File(MISSIONS_FILE);
        if (!file.exists()) {
            System.err.println("Missions file not found: " + MISSIONS_FILE);
            return missions;
        }

        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            Object parsed = SimpleJsonParser.parse(content);
            
            if (parsed instanceof List) {
                List<Object> list = (List<Object>) parsed;
                for (Object item : list) {
                    if (item instanceof Map) {
                        try {
                            missions.add(parseMission((Map<String, Object>) item));
                        } catch (Exception e) {
                            System.err.println("Error parsing mission: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return missions;
    }

    @SuppressWarnings("unchecked")
    private static Mission parseMission(Map<String, Object> data) {
        String id = (String) data.get("id");
        if (id == null) throw new IllegalArgumentException("Mission ID is missing");

        MissionBuilder builder = new MissionBuilder(id);
        
        if (data.containsKey("title")) builder.setTitle((String) data.get("title"));
        if (data.containsKey("description")) builder.setDescription((String) data.get("description"));
        
        if (data.containsKey("rewardCredits")) {
            int credits = ((Number) data.get("rewardCredits")).intValue();
            String info = (String) data.get("rewardInfo");
            builder.setReward(credits, info);
        }
        
        if (data.containsKey("type")) builder.setType(Mission.MissionType.valueOf((String) data.get("type")));
        if (data.containsKey("priority")) builder.setPriority(Mission.MissionPriority.valueOf((String) data.get("priority")));
        if (data.containsKey("urgency")) builder.setUrgency(Mission.MissionUrgency.valueOf((String) data.get("urgency")));
        if (data.containsKey("difficulty")) builder.setDifficulty(((Number) data.get("difficulty")).intValue());
        if (data.containsKey("nextScene")) builder.setNextScene((String) data.get("nextScene"));

        // Requirements
        if (data.containsKey("requirements")) {
            List<Object> reqs = (List<Object>) data.get("requirements");
            for (Object r : reqs) builder.addRequirement((String) r);
        }

        // Spawn Conditions
        if (data.containsKey("spawnConditions")) {
            Map<String, Object> sc = (Map<String, Object>) data.get("spawnConditions");
            if (sc.containsKey("minReputation")) builder.setMinReputation(((Number) sc.get("minReputation")).intValue());
            if (sc.containsKey("maxReputation")) builder.setMaxReputation(((Number) sc.get("maxReputation")).intValue());
            if (sc.containsKey("minNotoriety")) builder.setMinNotoriety(((Number) sc.get("minNotoriety")).intValue());
            if (sc.containsKey("maxNotoriety")) builder.setMaxNotoriety(((Number) sc.get("maxNotoriety")).intValue());
            if (sc.containsKey("minKarma")) builder.setMinKarma(((Number) sc.get("minKarma")).intValue());
            if (sc.containsKey("maxKarma")) builder.setMaxKarma(((Number) sc.get("maxKarma")).intValue());
            
            if (sc.containsKey("requiredFlags")) {
                List<Object> flags = (List<Object>) sc.get("requiredFlags");
                for (Object f : flags) builder.addRequiredFlag((String) f);
            }
            if (sc.containsKey("forbiddenFlags")) {
                List<Object> flags = (List<Object>) sc.get("forbiddenFlags");
                for (Object f : flags) builder.addForbiddenFlag((String) f);
            }
            if (sc.containsKey("requiredDistrictState")) {
                builder.setRequiredDistrictState((String) sc.get("requiredDistrictState"));
            }
        }

        // Consequences
        if (data.containsKey("consequences")) {
             Map<String, Object> cons = (Map<String, Object>) data.get("consequences");
             if (cons.containsKey("districtChange")) {
                 builder.setDistrictChange((String) cons.get("districtChange"));
             }
             if (cons.containsKey("reputationDelta")) {
                 builder.setReputationDelta(((Number) cons.get("reputationDelta")).intValue());
             }
             if (cons.containsKey("flagsToSet")) {
                 List<Object> flags = (List<Object>) cons.get("flagsToSet");
                 for (Object f : flags) builder.addFlagToSet((String) f);
             }
             if (cons.containsKey("flagsToClear")) {
                 List<Object> flags = (List<Object>) cons.get("flagsToClear");
                 for (Object f : flags) builder.addFlagToClear((String) f);
             }
             if (cons.containsKey("narrativeItems")) {
                 List<Object> items = (List<Object>) cons.get("narrativeItems");
                 for (Object i : items) builder.addNarrativeItem((String) i);
             }
        }

        return builder.build();
    }
}
