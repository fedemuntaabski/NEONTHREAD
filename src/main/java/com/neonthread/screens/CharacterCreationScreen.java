package com.neonthread.screens;

import com.neonthread.Character;
import com.neonthread.GameConstants;
import com.neonthread.GameSession;
import com.neonthread.GameState;
import com.neonthread.ui.CyberpunkButton;
import com.neonthread.ui.CyberpunkComboBox;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Pantalla de creación de personaje.
 * Usa Strategy pattern en selección de roles y Builder pattern en construcción de UI.
 */
public class CharacterCreationScreen extends JPanel {
    private final Consumer<GameState> onComplete;
    
    private JTextField nameField;
    private CyberpunkComboBox<Character.Role> roleCombo;
    private CyberpunkComboBox<Character.Difficulty> difficultyCombo;
    
    // Labels para mostrar selecciones
    private JLabel selectedRoleLabel;
    private JLabel selectedDifficultyLabel;
    
    // Panel de atributos dinámicos
    private JLabel intelligenceLabel;
    private JLabel physicalLabel;
    private JLabel perceptionLabel;
    private JLabel charismaLabel;
    
    public CharacterCreationScreen(Consumer<GameState> onComplete) {
        this.onComplete = onComplete;
        setLayout(new BorderLayout());
        setBackground(GameConstants.COLOR_BACKGROUND);
        
        buildUI();
        
        // Inicializar labels de selección
        updateSelectedRoleLabel();
        updateSelectedDifficultyLabel();
    }
    
    private void buildUI() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(GameConstants.COLOR_BACKGROUND);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(60, 150, 60, 150));
        
        // Título
        JLabel titleLabel = new JLabel("OPERATOR IDENTITY");
        titleLabel.setFont(GameConstants.FONT_TITLE.deriveFont(28f));
        titleLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(35));
        
        // Campo de nombre
        centerPanel.add(createFieldPanel("Operator Name:", nameField = createTextField()));
        centerPanel.add(Box.createVerticalStrut(18));
        
        // Selector de rol con listener
        roleCombo = new CyberpunkComboBox<>(Character.Role.values());
        roleCombo.addActionListener(e -> {
            updateAttributes();
            updateSelectedRoleLabel();
        });
        centerPanel.add(createFieldPanel("Initial Role:", roleCombo));
        centerPanel.add(Box.createVerticalStrut(18));
        
        // Panel de atributos base (auto-ajustados por rol)
        centerPanel.add(createAttributesPanel());
        centerPanel.add(Box.createVerticalStrut(18));
        
        // Selector de dificultad con explicación
        difficultyCombo = new CyberpunkComboBox<>(Character.Difficulty.values());
        difficultyCombo.setSelectedIndex(1); // Normal por defecto
        difficultyCombo.addActionListener(e -> updateSelectedDifficultyLabel());
        centerPanel.add(createDifficultyPanel());
        centerPanel.add(Box.createVerticalStrut(25));
        
        // Botones
        JPanel buttonPanel = createButtonPanel();
        centerPanel.add(buttonPanel);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font(GameConstants.FONT_FAMILY, Font.PLAIN, 18));
        field.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        field.setBackground(GameConstants.COLOR_PANEL);
        field.setCaretColor(GameConstants.COLOR_CYAN_NEON);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GameConstants.COLOR_CYAN_NEON, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setMaximumSize(new Dimension(400, 40));
        // Límite de 20 caracteres
        field.setDocument(new javax.swing.text.PlainDocument() {
            @Override
            public void insertString(int offs, String str, javax.swing.text.AttributeSet a) 
                    throws javax.swing.text.BadLocationException {
                if (str != null && getLength() + str.length() <= 20) {
                    super.insertString(offs, str, a);
                }
            }
        });
        return field;
    }
    
    /**
     * Crea el panel de atributos base (DRY).
     */
    private JPanel createAttributesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(GameConstants.COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GameConstants.COLOR_CYAN_NEON.darker(), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(500, 200));
        
        // Título horizontal con rol seleccionado
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        titlePanel.setBackground(GameConstants.COLOR_BACKGROUND);
        
        JLabel titleLabel = new JLabel("Base Attributes:");
        titleLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 14));
        titleLabel.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        
        selectedRoleLabel = new JLabel("NONE");
        selectedRoleLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 14));
        selectedRoleLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        
        titlePanel.add(titleLabel);
        titlePanel.add(selectedRoleLabel);
        panel.add(titlePanel);
        panel.add(Box.createVerticalStrut(12));
        
        // Atributos
        intelligenceLabel = createAttributeLabel("Intelligence: 5");
        physicalLabel = createAttributeLabel("Physical: 1");
        perceptionLabel = createAttributeLabel("Perception: 3");
        charismaLabel = createAttributeLabel("Charisma: 2");
        
        panel.add(intelligenceLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(physicalLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(perceptionLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(charismaLabel);
        
        return panel;
    }
    
    private JLabel createAttributeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(GameConstants.FONT_FAMILY, Font.PLAIN, 16));
        label.setForeground(GameConstants.COLOR_CYAN_NEON);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Agregar tooltips explicativos para cada atributo
        if (text.contains("Intelligence")) {
            label.setToolTipText("Intelligence: Affects success in hacks, puzzle solving and technical negotiation");
        } else if (text.contains("Physical")) {
            label.setToolTipText("Physical: Determines combat resistance, carrying capacity and maximum health");
        } else if (text.contains("Perception")) {
            label.setToolTipText("Perception: Improves threat detection, information gathering and stealth");
        } else if (text.contains("Charisma")) {
            label.setToolTipText("Charisma: Influences dialogue, persuasion and NPC relationships");
        }
        
        return label;
    }
    
    /**
     * Actualiza los atributos mostrados según el rol seleccionado (KISS).
     */
    private void updateAttributes() {
        Character.Role role = (Character.Role) roleCombo.getSelectedItem();
        if (role == null) return;
        
        switch (role) {
            case HACKER:
                intelligenceLabel.setText("Intelligence: 5");
                physicalLabel.setText("Physical: 1");
                perceptionLabel.setText("Perception: 3");
                charismaLabel.setText("Charisma: 2");
                break;
            case MERC:
                intelligenceLabel.setText("Intelligence: 2");
                physicalLabel.setText("Physical: 5");
                perceptionLabel.setText("Perception: 3");
                charismaLabel.setText("Charisma: 1");
                break;
            case INFO_BROKER:
                intelligenceLabel.setText("Intelligence: 3");
                physicalLabel.setText("Physical: 1");
                perceptionLabel.setText("Perception: 4");
                charismaLabel.setText("Charisma: 4");
                break;
        }
    }
    
    /**
     * Actualiza el label del rol seleccionado.
     */
    private void updateSelectedRoleLabel() {
        Character.Role role = (Character.Role) roleCombo.getSelectedItem();
        if (role != null && selectedRoleLabel != null) {
            selectedRoleLabel.setText(role.toString());
        }
    }
    
    /**
     * Actualiza el label de la dificultad seleccionada.
     */
    private void updateSelectedDifficultyLabel() {
        Character.Difficulty difficulty = (Character.Difficulty) difficultyCombo.getSelectedItem();
        if (difficulty != null && selectedDifficultyLabel != null) {
            selectedDifficultyLabel.setText(difficulty.toString());
        }
    }
    
    private JPanel createFieldPanel(String labelText, JComponent component) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(GameConstants.COLOR_BACKGROUND);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 16));
        label.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(label);
        panel.add(Box.createVerticalStrut(8));
        panel.add(component);
        
        return panel;
    }
    
    private JPanel createDifficultyPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(GameConstants.COLOR_BACKGROUND);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Panel horizontal para título y botón de info
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        headerPanel.setBackground(GameConstants.COLOR_BACKGROUND);
        
        // Label del título
        JLabel label = new JLabel("Difficulty:");
        label.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 16));
        label.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        
        // Botón de información con tooltip
        CyberpunkButton infoButton = new CyberpunkButton("?");
        infoButton.setPreferredSize(new Dimension(30, 30));
        infoButton.setToolTipText("<html>Easy: +50% credits, -25% damage received<br>Normal: Balanced rewards and challenges<br>Hard: -25% credits, +50% damage received</html>");
        
        headerPanel.add(label);
        headerPanel.add(infoButton);
        panel.add(headerPanel);
        panel.add(Box.createVerticalStrut(8));
        
        // Combo box
        difficultyCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(difficultyCombo);
        panel.add(Box.createVerticalStrut(5));
        
        // Dificultad seleccionada
        selectedDifficultyLabel = new JLabel("NORMAL");
        selectedDifficultyLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 14));
        selectedDifficultyLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        selectedDifficultyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(selectedDifficultyLabel);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setBackground(GameConstants.COLOR_BACKGROUND);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        CyberpunkButton confirmButton = new CyberpunkButton("CONFIRM");
        confirmButton.addActionListener(e -> confirmCharacter());
        
        CyberpunkButton backButton = new CyberpunkButton("BACK");
        backButton.addActionListener(e -> onComplete.accept(GameState.STATE_MENU));
        
        panel.add(confirmButton);
        panel.add(backButton);
        
        return panel;
    }
    
    private void confirmCharacter() {
        String name = nameField.getText().trim();
        
        // Validación: al menos 1 carácter
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "You must enter a name for the operator.\n(Minimum 1 character)",
                "Incomplete Data",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Character.Role role = (Character.Role) roleCombo.getSelectedItem();
        Character.Difficulty difficulty = (Character.Difficulty) difficultyCombo.getSelectedItem();
        
        // Crear personaje y comenzar sesión
        Character character = new Character(name, role, difficulty);
        GameSession.getInstance().startNewGame(character);
        
        // Avanzar al intro narrativo con transición
        onComplete.accept(GameState.STATE_INTRO_NARRATIVE);
    }
    
    /**
     * Resetea el formulario.
     */
    public void reset() {
        nameField.setText("");
        roleCombo.setSelectedIndex(0);
        difficultyCombo.setSelectedIndex(1); // Normal por defecto
        updateAttributes(); // Actualizar atributos al rol inicial
        updateSelectedRoleLabel(); // Actualizar label del rol
        updateSelectedDifficultyLabel(); // Actualizar label de la dificultad
    }
}
