# 🌆 NEONTHREAD: PROTOCOL 07

Un juego minimalista estilo Cyberpunk 2077 desarrollado en Java con Swing.

## � Estado del Roadmap Técnico

### ✅ FASE 1 - PULIDO CRÍTICO (100%)
- ✅ TransitionOverlay: Sistema de transiciones visuales con fade
- ✅ CharacterSummaryPanel: Confirmación explícita con validación
- ✅ TutorialHint: Tutorial implícito sin tutorial formal
- ✅ StatFeedbackService: Feedback visual de impacto de stats

### ✅ FASE 2 - CONSISTENCIA Y PROFUNDIDAD (100%)
- ✅ RunMemory: Sistema de recuerdos de decisiones/consecuencias
- ✅ DistrictModifier: Consecuencias visuales en el distrito
- ✅ IntroLoader: Variantes narrativas por rol y dificultad
- ✅ MissionWindow: Risk assessment, outcomes y requisitos mejorados

### 🔄 FASE 3 - ESCALABILIDAD (En progreso)
- ⏳ Feature 9: Refactor completo a Data-Driven
- ⏳ Feature 10: Sistema de Guardado de Run
- ⏳ Feature 11: Sistema de Reputación Local
- ⏳ Feature 12: Debug Overlay (modo dev)

## �🎨 Descripción

NEONTHREAD es un juego RPG cyberpunk desarrollado en Java con Swing, que combina narrativa interactiva con gestión de misiones, inventario y progresión de personaje. Con una estética minimalista neón y un sistema de estados robusto.

### Características Principales

- 🖥️ **Secuencia de arranque cinematográfica** con efectos [NO SIGNAL] y flash
- ⚡ **Boot log tipo BIOS futurista** con glitches y warnings
- 🎯 **Logo corporativo ASCII** con efectos de corrupción visual
- 🌌 **Menú holográfico** con borde cian y efectos de interferencia
- 🗺️ **Sistema de mapa de distrito** interactivo con misiones y locaciones
- 📖 **Narrativa ramificada** con sistema de nodos y consecuencias
- 🎭 **Creación de personaje** con roles (Netrunner, Solo, Techie, Fixer, Corpo)
- 📦 **Sistema de inventario** con ítems, modificadores y upgrades
- 🎯 **Sistema de misiones** completo con requisitos, recompensas y condiciones
- ⚙️ **Configuración completa** (Video, Audio, Gameplay, Controles, Accesibilidad)
- 🌍 **Sistema de localización** (i18n) con soporte multiidioma
- 📊 **Estadísticas y progresión** con atributos base y capacidades derivadas
- 🔄 **Patrón MVP** para separación de lógica y presentación
- ✨ **Efectos visuales avanzados** (typewriter, glitch, fade-in, scanlines)
- 🎨 **Paleta cyberpunk mejorada** con colores neón vibrantes

## 🚀 Requisitos

- Java 8 o superior
- Sistema operativo: Windows, macOS o Linux

## 📦 Instalación

### Compilar y ejecutar

```bash
# Navegar al directorio del proyecto
cd NEONTHREAD

# Compilar todos los archivos Java
javac -cp . -d bin (Get-ChildItem -Recurse src/main/java -Filter *.java).FullName

# O en Linux/Mac:
find src/main/java -name "*.java" | xargs javac -d bin

# Ejecutar el juego
java -cp bin com.neonthread.NeonThreadGame
```

### Usando Maven (opcional)

Si prefieres usar Maven, crea un archivo `pom.xml` y ejecuta:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.neonthread.NeonThreadGame"
```

## 🎮 Controles

### Generales
- **↑/↓/←/→** - Navegar por menús, opciones y mapa
- **1-9** o **Enter** - Seleccionar opción/misión
- **ESC** - Pausa / Salir de settings
- **Click** - Interactuar con UI, botones, sliders

### Mapa de Distrito
- **I** - Abrir inventario
- **M** - Seleccionar misión más cercana al cursor
- **Click en misión** - Ver detalles de misión
- **Scroll/Drag** - Navegar por el mapa

### Narrativa
- **Click** - Saltar efecto typewriter
- **Botones de opción** - Elegir acciones (con checks de atributos)

NEONTHREAD incluye un sistema de configuración completo y funcional con 5 categorías:

### 📺 Video Settings
- **Resolution** - Cambia la resolución de la ventana en tiempo real (1024x768 a 2560x1440)
- **Window Mode** - Alterna entre Windowed, Borderless y Fullscreen
- **VSync** - Activa/desactiva sincronización vertical
- **Brightness** - Ajusta el brillo con preview visual en tiempo real
- **UI Scale** - Escala la interfaz de 80% a 200%

### 🔊 Audio Settings
- **Master/Music/SFX/Voice Volume** - Sliders independientes de 0-100%
- **Dynamic Mix** - Reduce la música durante diálogos
- **Test Sound** - Botón para probar el volumen actual

### 🎮 Gameplay Settings
- **Text Speed** - Controla la velocidad del efecto typewriter (Slow/Normal/Fast)
- **Auto-Advance** - Avanza diálogos automáticamente
- **Show Confirmed Choices** - Muestra las elecciones seleccionadas
- **Difficulty** - Story, Balanced o Hardcore
- **Permadeath Mode** - Activa modo muerte permanente
- **Glitch Intensity** - Controla la intensidad de los efectos glitch (0-100%)

### 🎯 Controls Settings
- **Key Rebinding** - Reasigna teclas para Interact, Inventory, Map y Advance Dialogue
- **Cursor Sensitivity** - Ajusta la sensibilidad del cursor
- **Keyboard-Only Mode** - Desactiva el mouse

### ♿ Accessibility Settings
- **High Contrast Mode** - Activa modo de alto contraste
- **Font Size** - Ajusta el tamaño de fuente (100%, 120%, 150%)
- **Disable Glitch Effects** - Desactiva completamente los efectos glitch
- **Wide Subtitles** - Subtítulos más anchos para mejor legibilidad
- **Text Guide Lines** - Líneas guía para facilitar la lectura

### 🔘 Funcionalidades del Menú Settings
- **APPLY** - Aplica los cambios inmediatamente al juego
- **SAVE** - Guarda la configuración y regresa al menú
- **DEFAULTS** - Restaura valores predeterminados
- **CANCEL** - Descarta cambios y regresa
- **[SAVING…]** - Indicador visual de guardado automático

> **Nota:** Los cambios de resolución y modo de ventana se aplican inmediatamente al presionar APPLY. Algunos cambios pueden requerir reiniciar el juego.

## 🎬 Flujo de Pantallas

1. **Bootstrap** → Pantalla [NO SIGNAL] con fade-in + flash blanco
2. **Boot Log** → Secuencia tipo BIOS con glitches y warnings
3. **Logo Glitch** → ASCII art corporativo con efecto de corrupción
4. **Title** → Título con tagline cinematográfico
5. **Menu** → Menú holográfico con información de NightCity

## 📁 Estructura del Proyecto

```
NEONTHREAD/
├── src/main/java/com/neonthread/
│   ├── NeonThreadGame.java           # Clase principal + state manager
│   ├── GameState.java                # Estados del juego (14 estados)
│   ├── GameConstants.java            # Constantes y paleta de colores
│   ├── GameSession.java              # Singleton de sesión de juego
│   ├── GameSettings.java             # Configuración global
│   ├── Character.java                # Personaje del jugador
│   ├── Mission.java                  # Modelo de misión
│   ├── MissionBuilder.java           # Builder pattern para misiones
│   ├── NarrativeScene.java           # Escenas narrativas con nodos
│   ├── District.java                 # Distrito del juego
│   ├── WorldState.java               # Estado del mundo
│   ├── TypewriterEffect.java         # Efecto typewriter (DRY)
│   ├── BlinkingCursor.java           # Cursor parpadeante (DRY)
│   ├── GlitchEffect.java             # Efectos glitch (DRY)
│   │
│   ├── screens/                      # Pantallas del juego
│   │   ├── BootstrapScreen.java      # [NO SIGNAL] + flash
│   │   ├── BootScreen.java           # Boot log BIOS
│   │   ├── LogoScreen.java           # Logo glitcheado
│   │   ├── MenuScreen.java           # Menú principal
│   │   ├── SettingsScreen.java       # Configuración
│   │   ├── CharacterCreationScreen.java  # Creación de personaje
│   │   ├── IntroNarrativeScreen.java     # Narrativa de intro
│   │   ├── DistrictMapScreen.java        # HUB principal con mapa
│   │   ├── MissionWindowScreen.java      # Ventana de misión (MVP)
│   │   ├── MissionWindowPresenter.java   # Presenter (MVP pattern)
│   │   ├── NarrativeSceneScreen.java     # Escenas narrativas interactivas
│   │   ├── InventoryScreen.java          # Gestión de inventario
│   │   ├── ResultScreen.java             # Resultados post-misión
│   │   ├── LoadingRunScreen.java         # Pantalla de carga
│   │   ├── PauseScreen.java              # Menú de pausa
│   │   └── ShopScreen.java               # Tienda de upgrades
│   │
│   ├── ui/                           # Componentes UI reutilizables
│   │   ├── CyberpunkButton.java      # Botón personalizado
│   │   ├── CyberpunkSlider.java      # Slider con estilo cyberpunk
│   │   ├── CyberpunkComboBox.java    # Dropdown personalizado
│   │   ├── MissionBadge.java         # Badge para misiones (Builder)
│   │   ├── MissionSection.java       # Sección reutilizable
│   │   └── MissionCard.java          # Card modal
│   │
│   ├── inventory/                    # Sistema de inventario
│   │   ├── Inventory.java            # Inventario del jugador
│   │   ├── InventoryItem.java        # Ítems del juego
│   │   ├── ItemRegistry.java         # Registro de ítems (Factory)
│   │   ├── Upgrade.java              # Upgrades del personaje
│   │   └── UpgradeManager.java       # Gestor de upgrades
│   │
│   ├── stats/                        # Sistema de estadísticas
│   │   ├── BaseAttributes.java       # Atributos base
│   │   ├── DerivedCapabilities.java  # Capacidades derivadas
│   │   ├── RuntimeStats.java         # Stats en runtime
│   │   ├── StatType.java             # Tipos de estadísticas
│   │   ├── Modifier.java             # Modificadores de stats
│   │   └── StatEffectApplier.java    # Aplicador de efectos
│   │
│   ├── settings/                     # Sistema de configuración
│   │   ├── VideoSettings.java
│   │   ├── AudioSettings.java
│   │   ├── GameplaySettings.java
│   │   ├── LocalizationSettings.java
│   │   ├── AccessibilitySettings.java
│   │   └── appliers/                 # Strategy pattern
│   │       ├── VideoSettingsApplier.java
│   │       ├── AudioSettingsApplier.java
│   │       └── ...
│   │
│   ├── loaders/                      # Cargadores de datos
│   │   ├── MissionLoader.java        # Carga misiones desde JSON
│   │   └── SceneLoader.java          # Carga escenas desde JSON
│   │
│   ├── localization/                 # Sistema i18n
│   │   └── Localization.java         # Gestor de traducciones
│   │
│   ├── map/                          # Sistema de mapa
│   │   ├── MapConfig.java
│   │   └── MapConfigLoader.java
│   │
│   └── utils/                        # Utilidades
│       └── SimpleJsonParser.java     # Parser JSON ligero
│
├── config/                           # Archivos de configuración
│   ├── missions.json                 # Definiciones de misiones
│   ├── scenes.json                   # Escenas narrativas
│   ├── map.json                      # Configuración del mapa
│   ├── settings.properties           # Settings guardados
│   ├── lang/
│   │   ├── lang_en.properties        # Textos en inglés
│   │   └── lang_es.properties        # Textos en español
│   └── themes/                       # Temas visuales
│
├── bin/                              # Archivos compilados (.class)
├── .gitignore
└── README.md
```

## 🎨 Paleta de Colores

| Color | Hex | Uso |
|-------|-----|-----|
| Negro | `#000000` | Fondo |
| Cian Neón | `#00F7FF` | Texto principal/bordes |
| Magenta Neón | `#FF00E6` | Selección/glitches |
| Amarillo Neón | `#FFE600` | Advertencias/warnings |
| Azul Eléctrico | `#00A4FF` | Acentos |
| Gris Sistema | `#303030` | Info secundaria |

## 🛠️ Principios de Desarrollo

Este proyecto sigue:

- **KISS** (Keep It Simple, Stupid) - Código simple y directo
- **DRY** (Don't Repeat Yourself) - Componentes reutilizables (TypewriterEffect, GlitchEffect, BlinkingCursor)
- **State Machine Pattern** - Flujo de estados claro y mantenible
- **Separación de responsabilidades** - Cada clase tiene un propósito único

## ✨ Efectos Implementados

### 🔸 Fade-in
Aparición gradual del texto [NO SIGNAL] desde transparente

### 🔸 Flash blanco
Destello rápido de 50ms para transiciones dramáticas

### 🔸 Typewriter
Escritura letra por letra con delay configurable

### 🔸 Glitch de texto
Corrupción visual de caracteres con símbolos cyberpunk aleatorios

### 🔸 Cursor parpadeante
Cursor terminal que parpadea cada 600ms

### 🔸 Scanlines
Mensajes temporales de sistema e interferencias

### 🔸 Data Stream
Mensajes aleatorios de interferencia de señal

## ✨ Nuevas Features (FASE 1 - PULIDO CRÍTICO)

### 🎯 Sistema de Transiciones Visuales
- **TransitionOverlay**: Feedback visual suave entre pantallas clave
- Mensajes contextuales durante transiciones:
  - `INITIALIZING NEURAL LINK...` (Menu → Start Run)
  - `CONNECTING TO THE NETWORK...` (Intro → District Map)
  - `ACCESSING MISSION DATA...` (Map → Mission Window)
- Fade-in/fade-out automático (150ms)

### ✅ Confirmación de Personaje
- **CharacterSummaryPanel**: Pantalla de resumen antes de iniciar run
- Muestra:
  - Nombre del operador
  - Rol y descripción
  - Atributos base con barras visuales
  - Dificultad seleccionada
- Botones CONFIRM RUN / EDIT para confirmar o volver
- Validación visual con borde rojo si el nombre está vacío

### 🎓 Tutorial Implícito
- **TutorialHint**: Hints visuales no intrusivos
- Primera misión marcada como tutorial (flag `isTutorial`)
- Mensaje `[ CLICK ★ TO VIEW MISSION ]` al entrar al distrito
- Auto-desaparece tras 10 segundos o al aceptar misión
- Animación de pulse para llamar la atención

### 📊 Feedback de Stats
- **StatFeedbackService**: Sistema centralizado de feedback
- Logs automáticos en GameLog:
  - `✓ [INTELLIGENCE SUCCESS] 5/3`
  - `✗ [PHYSICAL FAILED] 2/4`
- Colores diferenciados:
  - Cyan (#00FFE7) para éxitos
  - Magenta (#FF00E6) para fallos
- Feedback visible durante checks narrativos

## 🔮 Estado Actual

### ✅ Completado

**Core Systems**
- ✅ Arquitectura de estados completa (14 estados)
- ✅ Sistema de sesión de juego (GameSession singleton)
- ✅ Sistema de configuración global (GameSettings)
- ✅ Gestión de estado del mundo (WorldState)

**Pantallas y UI**
- ✅ Secuencia bootstrap con fade y flash
- ✅ Boot log tipo BIOS con glitches
- ✅ Logo corporativo con efecto glitch
- ✅ Menú principal holográfico
- ✅ Sistema de Settings completo (5 categorías)
- ✅ Creación de personaje con roles
- ✅ Mapa de distrito interactivo (HUB principal)
- ✅ Sistema de misiones con ventana de detalles (MVP pattern)
- ✅ Narrativa interactiva con sistema de nodos
- ✅ Inventario con ítems y modificadores
- ✅ Pantalla de resultados post-misión
- ✅ Pantalla de pausa
- ✅ Tienda de upgrades

**Sistemas de Juego**
- ✅ Sistema de personaje con stats (base + derivados)
- ✅ Sistema de progresión (level, XP, karma, notoriedad)
- ✅ Sistema de misiones completo (requisitos, spawn conditions)
- ✅ Sistema de narrativa ramificada (checks de atributos)
- ✅ Sistema de inventario (ítems, upgrades, modificadores)
- ✅ Sistema de consecuencias (flags, credits, items)
- ✅ GameLog para seguimiento de eventos

**Patterns y Arquitectura**
- ✅ MVP Pattern (MissionWindowPresenter)
- ✅ Builder Pattern (MissionBuilder, UI components)
- ✅ Factory Pattern (ItemRegistry)
- ✅ Strategy Pattern (SettingsAppliers, ThemeEngine)
- ✅ Singleton Pattern (GameSession, GameSettings)
- ✅ Observer Pattern (GameSettings listeners)
- ✅ State Pattern (BootstrapScreen, NarrativeSceneScreen)

**Data y Localización**
- ✅ Cargador de misiones desde JSON
- ✅ Cargador de escenas desde JSON
- ✅ Sistema i18n con soporte multiidioma
- ✅ Parser JSON ligero sin dependencias

**Componentes UI Reutilizables**
- ✅ CyberpunkButton, Slider, ComboBox
- ✅ MissionBadge, MissionSection, MissionCard
- ✅ Efectos: Typewriter, Glitch, BlinkingCursor

### ⏳ Pendiente

- ⏳ Sistema de guardado persistente
- ⏳ Audio engine y efectos de sonido
- ⏳ Más misiones y contenido narrativo
- ⏳ Sistema de combate (si aplica)
- ⏳ Más locaciones en el mapa
- ⏳ Animaciones avanzadas

## 📝 Licencia

Este proyecto es de código abierto y está disponible para uso educativo.

## 👤 Autor

Desarrollado con ❤️ siguiendo la estética Cyberpunk 2077

---

**> Every runner leaves a trace. Yours starts now.**
