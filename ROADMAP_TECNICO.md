# 🧭 ROADMAP TÉCNICO – NEONTHREAD: PROTOCOL 07

## 📋 Visión General

Este roadmap define el plan de mejora y escalabilidad del proyecto NEONTHREAD siguiendo mejores prácticas de desarrollo de software. El objetivo es reforzar la arquitectura existente, mejorar la experiencia del usuario y preparar el proyecto para crecer sin necesidad de reescribirlo.

---

## 🟢 FASE 1 — PULIDO CRÍTICO (CORTO PLAZO) ✅ COMPLETADA

**Objetivo**: Mejorar UX, claridad y feedback sin agregar sistemas grandes.

### 1. ✅ Feedback Visual y de Estado en START RUN

**Problema**: El flujo es sólido, pero hay acciones sin confirmación clara.

**Implementación Completada**:
- ✅ **TransitionOverlay** reutilizable con fade-in/fade-out
  - Clase: `com.neonthread.ui.TransitionOverlay`
  - Fade duration: 150ms
  - Display time: 300ms
- ✅ Transiciones automáticas entre pantallas clave:
  - Menu → LoadingRun: "INITIALIZING NEURAL LINK..."
  - Intro → DistrictMap: "CONNECTING TO THE NETWORK..."
  - Character Creation → Intro: "SYNCHRONIZING IDENTITY..."
  - Map → Mission Window: "ACCESSING MISSION DATA..."
  - Map → Narrative Scene: "LOADING SCENARIO..."
- ✅ Integración en `NeonThreadGame.changeState()` con lógica centralizada

**Archivos Modificados**:
- `src/main/java/com/neonthread/NeonThreadGame.java`
- `src/main/java/com/neonthread/ui/TransitionOverlay.java` (nuevo)

---

### 2. ✅ Confirmación Explícita en Character Creation

**Problema**: CONFIRM crea el personaje sin feedback fuerte.

**Implementación Completada**:
- ✅ **CharacterSummaryPanel** - Pantalla de confirmación intermedia
  - Clase: `com.neonthread.screens.CharacterSummaryPanel`
  - Muestra: nombre, rol, atributos base, dificultad
  - Botones: CONFIRM RUN / EDIT
- ✅ Validación visual mejorada:
  - Borde rojo en campo de nombre vacío
  - Tooltip con mensaje de error
- ✅ Flujo de navegación bidireccional (confirmar o editar)

**Archivos Modificados**:
- `src/main/java/com/neonthread/screens/CharacterCreationScreen.java`
- `src/main/java/com/neonthread/screens/CharacterSummaryPanel.java` (nuevo)

---

### 3. ✅ Tutorial Implícito (Sin Tutorial)

**Problema**: El jugador no sabe qué hacer al llegar al mapa.

**Implementación Completada**:
- ✅ **Mission Flags**: Nuevo campo `isTutorial` en clase `Mission`
- ✅ Primera misión marcada automáticamente como tutorial
- ✅ **TutorialHint** - Componente visual con:
  - Clase: `com.neonthread.ui.TutorialHint`
  - Mensaje: "[ CLICK ★ TO VIEW MISSION ]"
  - Animación de pulse (40-100% opacity)
  - Auto-hide tras 10 segundos
  - Posicionado dinámicamente en el centro-inferior
- ✅ Integración en `DistrictMapScreen.refresh()`

**Archivos Modificados**:
- `src/main/java/com/neonthread/Mission.java`
- `src/main/java/com/neonthread/MissionBuilder.java`
- `src/main/java/com/neonthread/GameSession.java`
- `src/main/java/com/neonthread/screens/DistrictMapScreen.java`
- `src/main/java/com/neonthread/ui/TutorialHint.java` (nuevo)

---

### 4. ✅ Feedback Cuando un Stat Afecta Algo

**Problema**: Los stats existen, pero su impacto es invisible.

**Implementación Completada**:
- ✅ **StatFeedbackService** - Servicio centralizado de feedback
  - Clase: `com.neonthread.services.StatFeedbackService`
  - Métodos:
    - `logSuccess(StatType, required, actual)` → Registra éxito
    - `logFailure(StatType, required, actual)` → Registra fallo
    - `formatUIMessage(success, type, description)` → HTML formateado
    - `getFeedbackColor(success)` → Color apropiado
- ✅ Logs automáticos en `GameLog`:
  - `✓ [INTELLIGENCE SUCCESS] 5/3`
  - `✗ [PHYSICAL FAILED] 2/4`
- ✅ Colores diferenciados:
  - Cyan (#00FFE7) para éxitos
  - Magenta (#FF00E6) para fallos
- ✅ Integración en `NarrativeSceneScreen.evaluateChecksWithFeedback()`

**Archivos Modificados**:
- `src/main/java/com/neonthread/screens/NarrativeSceneScreen.java`
- `src/main/java/com/neonthread/services/StatFeedbackService.java` (nuevo)

---

## 🟡 FASE 2 — CONSISTENCIA Y PROFUNDIDAD (MEDIO PLAZO)

**Objetivo**: Hacer que decisiones tempranas tengan peso real.

### 5. Sistema de Recuerdos de Run

**Problema**: Las decisiones se olvidan rápido.

**Tareas Técnicas**:
- [ ] Crear clase `RunMemory`:
  - `List<MemoryEvent>` con eventos importantes
  - Cada evento: `id`, `texto`, `timestamp`, `tipo` (decisión/consecuencia)
- [ ] Integrar en `GameSession` como historial persistente
- [ ] Mostrar en:
  - `ResultScreen` - Resumen de decisiones clave
  - `PauseScreen` (opcional) - Acceso rápido al historial
- [ ] UI: Panel con scroll, iconografía por tipo de evento
- [ ] Serialización para guardado futuro

**Archivos a Crear**:
- `src/main/java/com/neonthread/RunMemory.java`
- `src/main/java/com/neonthread/MemoryEvent.java`

**Archivos a Modificar**:
- `src/main/java/com/neonthread/GameSession.java`
- `src/main/java/com/neonthread/screens/ResultScreen.java`
- `src/main/java/com/neonthread/screens/PauseScreen.java`

---

### 6. Consecuencias Visibles en el Distrito

**Problema**: `WorldState` cambia, pero el mapa no reacciona.

**Tareas Técnicas**:
- [ ] Crear clase `DistrictModifier`:
  - Aplica cambios visuales al mapa según flags
  - Modifica: misiones disponibles, NPCs, zonas bloqueadas
- [ ] Implementar método `District.applyWorldState(WorldState)`:
  - Filtra misiones según flags del mundo
  - Aplica modificadores visuales dinámicos
- [ ] Añadir eventos de distrito:
  - "Zona [X] bloqueada tras misión fallida"
  - "Nueva locación desbloqueada"
- [ ] UI: Indicadores visuales en el mapa (zonas grises, iconos de bloqueo)

**Archivos a Crear**:
- `src/main/java/com/neonthread/DistrictModifier.java`

**Archivos a Modificar**:
- `src/main/java/com/neonthread/District.java`
- `src/main/java/com/neonthread/screens/DistrictMapScreen.java`

---

### 7. Variantes Narrativas en Intro

**Problema**: La intro es buena, pero fija.

**Tareas Técnicas**:
- [ ] Mover texto de intro completamente a `scenes.json`:
  - Variantes según `rol` y `dificultad`
  - Campos: `intro_hacker`, `intro_merc`, `intro_info_broker`
- [ ] Añadir micro-decisiones tempranas:
  - No afectan gameplay directo
  - Setean flags narrativos (`intro_choice_aggressive`, etc.)
- [ ] Actualizar `IntroNarrativeScreen` para cargar desde JSON
- [ ] Crear escenas de intro parametrizadas con placeholders:
  - `{operator_name}`, `{role_description}`, `{district_name}`

**Archivos a Modificar**:
- `config/scenes.json`
- `src/main/java/com/neonthread/screens/IntroNarrativeScreen.java`
- `src/main/java/com/neonthread/loaders/SceneLoader.java`

---

### 8. Ventana de Misión Más Informativa

**Problema**: `MissionWindow` es correcta pero plana.

**Tareas Técnicas**:
- [ ] Añadir secciones nuevas en `MissionWindowScreen`:
  - **Riesgos**: Nivel de peligro, posibles consecuencias negativas
  - **Outcomes posibles**: Hints ocultos/parciales de resultados
  - **Requisitos**: Mostrar checks con tooltips descriptivos
- [ ] Iconografía textual:
  - `⚠ RISK LEVEL: HIGH`
  - `⛓ LOCKED BY PREVIOUS RUN`
  - `✓ TUTORIAL MISSION`
- [ ] Tooltip con requisitos no cumplidos:
  - "Requiere Intelligence ≥ 4 (tienes 3)"
- [ ] Colores contextuales:
  - Verde para requisitos cumplidos
  - Rojo para requisitos no cumplidos

**Archivos a Modificar**:
- `src/main/java/com/neonthread/screens/MissionWindowScreen.java`
- `src/main/java/com/neonthread/Mission.java` (añadir campo `riskLevel`)

---

## 🔵 FASE 3 — ESCALABILIDAD (LARGO PLAZO)

**Objetivo**: Preparar el proyecto para crecer sin reescribir.

### 9. Refactor Completo a Data-Driven

**Problema**: Aún hay lógica hardcodeada.

**Tareas Técnicas**:
- [ ] Mover a JSON:
  - **Distritos**: `config/districts.json` (nombre, locaciones, NPCs)
  - **Intro**: `config/intros.json` (variantes por rol/dificultad)
  - **Tutorial**: `config/tutorial.json` (hints, condiciones)
- [ ] Implementar validación de JSON al cargar:
  - Schema validation (campos requeridos)
  - Warnings visuales en consola si falta data
- [ ] Sistema de fallback robusto:
  - Si JSON falla, usar valores hardcodeados
  - Log de errores con `StatFeedbackService` o similar

**Archivos a Crear**:
- `config/districts.json`
- `config/intros.json`
- `config/tutorial.json`
- `src/main/java/com/neonthread/loaders/DistrictLoader.java`
- `src/main/java/com/neonthread/loaders/IntroLoader.java`

**Archivos a Modificar**:
- `src/main/java/com/neonthread/loaders/MissionLoader.java`
- `src/main/java/com/neonthread/loaders/SceneLoader.java`

---

### 10. Sistema de Guardado de Run

**Problema**: No hay persistencia.

**Tareas Técnicas**:
- [ ] Crear clase `SaveGame`:
  - Campos: `Character`, `WorldState`, `Mission[]`, `Inventory`, `RunMemory`
  - Serialización a JSON (`saves/save_slot_1.json`)
- [ ] Implementar `SaveManager`:
  - `save(SaveGame)` → Serializa y escribe archivo
  - `load(String slotId)` → Carga desde archivo
  - `listSaves()` → Devuelve lista de slots disponibles
- [ ] Auto-save:
  - Tras completar misión
  - Al cambiar de distrito
  - Al salir del juego
- [ ] UI: Pantalla "LOAD MEMORY" en menú principal

**Archivos a Crear**:
- `src/main/java/com/neonthread/SaveGame.java`
- `src/main/java/com/neonthread/SaveManager.java`

**Archivos a Modificar**:
- `src/main/java/com/neonthread/screens/MenuScreen.java`
- `src/main/java/com/neonthread/GameSession.java`

---

### 11. Sistema de Reputación Local

**Problema**: Reputación es global y abstracta.

**Tareas Técnicas**:
- [ ] Crear clase `FactionReputation`:
  - Asociar reputación a: Distritos, Fixers, Corporaciones
  - Campos: `factionId`, `reputationValue`, `tier` (Unknown/Neutral/Trusted/Hostile)
- [ ] Integrar en `WorldState`:
  - `Map<String, FactionReputation> factions`
- [ ] Afecta:
  - **Precios**: Descuentos/aumentos en tiendas según reputación
  - **Misiones**: Algunas misiones requieren reputación mínima
  - **Diálogos**: Opciones desbloqueadas por reputación
- [ ] UI: Indicador de reputación en `DistrictMapScreen`

**Archivos a Crear**:
- `src/main/java/com/neonthread/FactionReputation.java`

**Archivos a Modificar**:
- `src/main/java/com/neonthread/WorldState.java`
- `src/main/java/com/neonthread/screens/DistrictMapScreen.java`
- `src/main/java/com/neonthread/screens/ShopScreen.java`

---

### 12. Debug Overlay (Modo Dev)

**Problema**: Difícil depurar runs complejos.

**Tareas Técnicas**:
- [ ] Crear `DebugOverlay` activable con F12:
  - Muestra en panel transparente:
    - Estado actual (`currentState`)
    - Flags activos del `WorldState`
    - Stats reales del personaje
    - Misión activa
  - Botones rápidos:
    - Saltar a estado
    - Añadir créditos
    - Modificar stats
- [ ] Constante `DEV_MODE` en `GameConstants`:
  - Solo activo en desarrollo
  - Deshabilitado en builds de producción
- [ ] Shortcut: F12 para toggle

**Archivos a Crear**:
- `src/main/java/com/neonthread/ui/DebugOverlay.java`

**Archivos a Modificar**:
- `src/main/java/com/neonthread/GameConstants.java`
- `src/main/java/com/neonthread/NeonThreadGame.java`

---

## 🟣 FEATURES OPCIONALES (ALTO IMPACTO / BAJO COSTO)

### Daily News Feed

**Descripción**: Feed textual en el mapa con noticias generadas dinámicamente.

**Implementación**:
- [ ] Clase `NewsFeed` con generador de noticias según `WorldState`
- [ ] Plantillas de noticias con placeholders:
  - "MegaCorp anuncia nuevo protocolo de seguridad en {district}"
  - "Incidente en {location}: {consequence}"
- [ ] UI: Panel lateral en `DistrictMapScreen` con scroll

---

### NPCs Que Recuerdan Decisiones Pasadas

**Descripción**: NPCs referencian decisiones anteriores en diálogos.

**Implementación**:
- [ ] Extender `NarrativeScene` con campo `rememberedFlags`
- [ ] Diálogos con variantes según flags:
  - "Recuerdo que ayudaste a {npc_name}..."
  - "Después de lo que hiciste en {mission_name}..."
- [ ] Sistema de afinidad NPC (`npc_affinity_positive`, `npc_affinity_negative`)

---

### Ghost Runs

**Descripción**: Referencias sutiles a runs anteriores (si hay sistema de guardado).

**Implementación**:
- [ ] Al cargar run, detectar saves previos
- [ ] Añadir easter eggs textuales:
  - "Rastros de un operador previo en el sistema..."
  - Nombres de personajes anteriores en logs
- [ ] No afecta gameplay, solo inmersión

---

### Glitch Narrativo

**Descripción**: Si fallas checks críticos, texto sufre glitch visual.

**Implementación**:
- [ ] Detectar fallos críticos en `NarrativeSceneScreen`
- [ ] Aplicar `GlitchEffect` temporal al texto narrativo
- [ ] Duración: 2-3 segundos
- [ ] Feedback adicional de tensión

---

### Logs Tipo Terminal

**Descripción**: Acceso completo al `GameLog` desde menú de pausa.

**Implementación**:
- [ ] Botón "VIEW LOGS" en `PauseScreen`
- [ ] Pantalla modal con:
  - `JTextArea` con todo el log
  - Filtros: Éxitos, Fallos, Decisiones, Sistema
  - Búsqueda por palabra clave
- [ ] Estética de terminal (fondo negro, texto cyan)

---

## 📌 RESUMEN DE ESTADO

| Fase | Estado | Completado |
|------|--------|------------|
| **FASE 1 - Pulido Crítico** | ✅ Completa | 4/4 (100%) |
| **FASE 2 - Consistencia** | ⏳ Pendiente | 0/4 (0%) |
| **FASE 3 - Escalabilidad** | ⏳ Pendiente | 0/4 (0%) |
| **Features Opcionales** | ⏳ Pendiente | 0/5 (0%) |

---

## 🎯 PRÓXIMOS PASOS RECOMENDADOS

1. **Probar FASE 1 exhaustivamente**
   - Verificar transiciones visuales
   - Testear flujo de confirmación de personaje
   - Validar hints de tutorial
   - Comprobar logs de stats en narrativa

2. **Priorizar FASE 2 - Feature #5 (Recuerdos de Run)**
   - Mayor impacto narrativo
   - Relativamente simple de implementar
   - No requiere refactors grandes

3. **Documentar patrones emergentes**
   - Crear guía de estilo para nuevos componentes UI
   - Documentar convenciones de naming en JSON
   - Establecer template para nuevas pantallas

4. **Considerar migración a build system**
   - Maven o Gradle para gestión de dependencias
   - Reemplazar `SimpleJsonParser` por Gson/Jackson
   - Facilitar testing y distribución

---

## 📚 NOTAS TÉCNICAS

### Patrones de Diseño Utilizados

- **Service Layer**: `StatFeedbackService` - Lógica centralizada
- **Component Pattern**: `TransitionOverlay`, `TutorialHint` - UI reutilizable
- **Factory Method**: `MissionBuilder.setTutorial()` - Configuración fluida
- **Observer Pattern**: Auto-hide de hints, eventos de transición

### Principios Aplicados

- **DRY**: Componentes reutilizables (`TransitionOverlay`, `StatFeedbackService`)
- **KISS**: Soluciones simples y directas sin over-engineering
- **SOLID**:
  - Single Responsibility: Cada servicio tiene una función clara
  - Open/Closed: Extensible sin modificar código base
  - Dependency Inversion: Uso de callbacks y abstracciones

### Mejores Prácticas Seguidas

- ✅ Código autodocumentado con JavaDoc
- ✅ Constantes centralizadas en `GameConstants`
- ✅ Separación de responsabilidades (UI, lógica, servicios)
- ✅ Feedback visual consistente
- ✅ Manejo de estados predecible

---

## 🚀 CONCLUSIÓN

Este roadmap mantiene la identidad cyberpunk del proyecto mientras refuerza su arquitectura y escalabilidad. La **FASE 1 está completamente implementada**, proporcionando mejoras inmediatas en UX sin romper el código existente. Las fases siguientes están planificadas para implementarse de forma incremental, manteniendo el proyecto viable y profesional.

**> Every line of code matters. Build it right.**
