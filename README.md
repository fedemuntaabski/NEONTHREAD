# 🌆 NEONTHREAD: PROTOCOL 07

Un juego minimalista estilo Cyberpunk 2077 desarrollado en Java con Swing.

## 🎨 Descripción

NEONTHREAD es un juego de texto futurista que combina la estética cyberpunk con una interfaz minimalista. Sin imágenes, solo texto, paneles y efectos de neón simulados.

### Características

- 🖥️ **Secuencia de arranque cinematográfica** con efectos [NO SIGNAL] y flash
- ⚡ **Boot log tipo BIOS futurista** con glitches y warnings
- 🎯 **Logo corporativo ASCII** con efectos de corrupción visual
- 🌌 **Menú holográfico** con borde cian y efectos de interferencia
- 🔄 **Sistema de estados** limpio y mantenible (5 estados)
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
javac -d bin src/main/java/com/neonthread/*.java src/main/java/com/neonthread/screens/*.java

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

- **↑/↓** - Navegar por el menú
- **1-4** o **Enter** - Seleccionar opción
- **ESC** - Cancelar (en algunos diálogos)

## 🎬 Flujo de Pantallas

1. **Bootstrap** → Pantalla [NO SIGNAL] con fade-in + flash blanco
2. **Boot Log** → Secuencia tipo BIOS con glitches y warnings
3. **Logo Glitch** → ASCII art corporativo con efecto de corrupción
4. **Title** → Título con tagline cinematográfico
5. **Menu** → Menú holográfico con información de NightCity

## 📁 Estructura del Proyecto

```
NEONTHREAD/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── neonthread/
│                   ├── NeonThreadGame.java      # Clase principal + state manager
│                   ├── GameState.java           # 5 estados del juego
│                   ├── GameConstants.java       # Constantes centralizadas
│                   ├── TypewriterEffect.java    # Efecto typewriter (DRY)
│                   ├── BlinkingCursor.java      # Cursor parpadeante (DRY)
│                   ├── GlitchEffect.java        # Efectos de glitch (DRY)
│                   └── screens/
│                       ├── BootstrapScreen.java # [NO SIGNAL] + flash
│                       ├── BootScreen.java      # Boot log BIOS
│                       ├── LogoScreen.java      # Logo glitcheado
│                       ├── TitleScreen.java     # Título + tagline
│                       └── MenuScreen.java      # Menú holográfico
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

## 🔮 Estado Actual

✅ Secuencia bootstrap con fade y flash  
✅ Boot log tipo BIOS con glitches  
✅ Logo corporativo con efecto glitch  
✅ Pantalla de título mejorada  
✅ Menú holográfico con efectos  
✅ Sistema de información de NightCity  
✅ Efectos visuales avanzados  
⏳ Lógica de juego (próximamente)  
⏳ Sistema de guardado (próximamente)  
⏳ Configuración (próximamente)

## 📝 Licencia

Este proyecto es de código abierto y está disponible para uso educativo.

## 👤 Autor

Desarrollado con ❤️ siguiendo la estética Cyberpunk 2077

---

**> Every runner leaves a trace. Yours starts now.**
