package com.neonthread.loaders;

import com.neonthread.utils.SimpleJsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Carga intros narrativos variantes según el rol del personaje y dificultad.
 * Patrón: Factory + Template Method para generación de texto personalizado.
 * 
 * FASE 2 Feature 7: Variantes narrativas contextuales.
 */
public class IntroLoader {
    private static final String CONFIG_DIR = "config";
    
    /**
     * Carga el intro apropiado basado en el rol del personaje.
     */
    public static String loadIntroForCharacter(com.neonthread.Character character, com.neonthread.Character.Difficulty difficulty) {
        if (character == null) {
            return loadDefaultIntro();
        }
        
        com.neonthread.Character.Role role = character.getRole();
        String filename = getIntroFilename(role);
        
        try {
            return loadIntroFromFile(filename, character.getName(), difficulty);
        } catch (Exception e) {
            System.err.println("Failed to load intro for " + role + ": " + e.getMessage());
            return loadDefaultIntro();
        }
    }
    
    /**
     * Determina el archivo JSON según el rol.
     */
    private static String getIntroFilename(com.neonthread.Character.Role role) {
        switch (role) {
            case HACKER:
                return "intro_hacker.json";
            case MERC:
                return "intro_mercenary.json";
            case INFO_BROKER:
                return "intro_info_broker.json";
            default:
                return "intro_hacker.json";
        }
    }
    
    /**
     * Lee el archivo JSON y genera el texto con placeholders reemplazados.
     */
    @SuppressWarnings("unchecked")
    private static String loadIntroFromFile(String filename, String operatorName, com.neonthread.Character.Difficulty difficulty) throws IOException {
        Path path = Paths.get(CONFIG_DIR, filename);
        String content = new String(Files.readAllBytes(path), "UTF-8");
        
        Map<String, Object> json = (Map<String, Object>) SimpleJsonParser.parse(content);
        StringBuilder builder = new StringBuilder();
        
        // Añadir título si existe
        if (json.containsKey("title")) {
            builder.append("[ ").append(json.get("title")).append(" ]\n\n");
        }
        
        // Añadir líneas de texto
        List<Object> lines = (List<Object>) json.get("lines");
        if (lines != null) {
            for (Object lineObj : lines) {
                String line = lineObj.toString();
                line = replacePlaceholders(line, operatorName);
                builder.append(line).append("\n");
            }
        }
        
        // Añadir variante de dificultad si existe
        if (json.containsKey("difficultyVariants") && difficulty != null) {
            Map<String, Object> variants = (Map<String, Object>) json.get("difficultyVariants");
            String diffKey = difficulty.name();
            if (variants != null && variants.containsKey(diffKey)) {
                builder.append("\n> ").append(variants.get(diffKey)).append("\n");
            }
        }
        
        return builder.toString();
    }
    
    /**
     * Reemplaza placeholders en el texto.
     */
    private static String replacePlaceholders(String text, String operatorName) {
        return text
            .replace("{operator_name}", operatorName.toUpperCase())
            .replace("{role_description}", "operator");
    }
    
    /**
     * Intro genérico de fallback.
     */
    private static String loadDefaultIntro() {
        return "> Initializing access protocols...\n" +
               "> Authenticating operator...\n" +
               "> Synchronizing neural implants...\n" +
               "> Loading environment: District Theta-5...\n\n" +
               "────────────────────────────────────────────────────────────\n\n" +
               "The megacity of NEONFALL never sleeps.\n\n" +
               "The year is 2087. The city has no name.\n" +
               "Only corporate codes and numbered sectors.\n\n" +
               "This is your first run.\n\n" +
               "────────────────────────────────────────────────────────────\n\n" +
               "> MISSION 01 UNLOCKED: First Contact\n" +
               "> System status: OPERATIONAL\n\n" +
               "Initiation protocol complete. Welcome to the network.";
    }
}
