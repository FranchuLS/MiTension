# MiTensión

**MiTensión** es una aplicación de Android moderna diseñada para ayudar a los usuarios a llevar un control riguroso de su presión arterial. Permite registrar mediciones diarias, visualizar el historial en un calendario y recibir recordatorios para no olvidar las tomas.

## 📖 Cómo funciona la App

La aplicación implementa el concepto de **"Día Lógico"** para adaptarse mejor al ciclo de sueño y vigilia del usuario.

### ⏰ Lógica de Tiempos y Sesiones
El día no cambia a medianoche, sino a las **04:00 AM**. Esto evita que las mediciones tomadas tarde en la noche se registren en el día siguiente.

*   **Ajuste Automático:** Si realizas una medición entre las **00:00 y las 03:59 AM**, la aplicación la guardará automáticamente con la fecha del día anterior (fijada a las 23:59:59).
*   **Sesiones de Medición:**
    *   **Mañana:** 04:00 — 12:30.
    *   **Al medio día:** 12:31 — 19:00.
    *   **Noche:** 19:01 — 03:59 (del día siguiente).

## 🚀 Características

- **Registro de Mediciones:** Guarda valores de presión sistólica, diastólica y pulso (opcional).
- **Calendario Histórico:** Resumen visual por colores y una **Tabla Resumen** con los datos reales de las últimas sesiones.
- **Detalle Diario:** Consulta el desglose de todas las tomas de un día, agrupadas por sesión y con cálculo de medias automáticas.
- **Recordatorios:** Sistema de notificaciones mediante WorkManager para asegurar la constancia en las mediciones.
- **Interfaz Moderna:** Construida íntegramente con Jetpack Compose y siguiendo las guías de Material Design 3, optimizada para diferentes tamaños de pantalla.

## 📦 Generación de Versiones (Releases)

El proyecto utiliza **GitHub Actions** para compilar automáticamente el APK firmado. Sigue estos pasos para generar una nueva versión:

1.  **Actualizar Versión:** Incrementa `versionCode` y actualiza `versionName` en `app/build.gradle.kts`.
2.  **Confirmar y Subir:**
    ```sh
    git add .
    git commit -m "release: v1.1.0"
    git push origin master
    ```
3.  **Crear Etiqueta (Tag):** El workflow se activa con cualquier etiqueta que empiece por `v`.
    ```sh
    git tag v1.1.0
    git push origin v1.1.0
    ```
4.  **Obtener el APK:** Una vez finalizada la acción en GitHub, el APK estará disponible en la sección **Releases**.

## 🛠️ Tecnologías Utilizadas

- **Kotlin 2.0:** Lenguaje moderno con el nuevo plugin de Compose Compiler.
- **Jetpack Compose:** Toolkit moderno para la construcción de interfaces nativas.
- **Room Database:** Persistencia de datos local de forma robusta con soporte para migraciones manuales.
- **WorkManager:** Gestión de tareas en segundo plano para recordatorios y alarmas.
- **Navigation Compose:** Gestión de la navegación entre pantallas dentro de una Single Activity.
- **ViewModel & StateFlow:** Arquitectura recomendada por Android para la gestión del estado de la UI.
- **KSP (Kotlin Symbol Processing):** Para una generación de código más rápida (usado con Room).

## 📂 Estructura del Proyecto

El proyecto sigue una arquitectura limpia dividida por capas:

- `data/`: Contiene las entidades de Room, el DAO, la base de datos y el Repositorio que centraliza el acceso a los datos.
- `ui/`:
    - `screens/`: Pantallas principales (Medición, Calendario, Detalle).
    - `viewmodel/`: Lógica de negocio de la UI y gestión de estados.
    - `components/`: Componentes de Compose reutilizables (TensionCard, TablaResumen, etc).
    - `theme/`: Definición de colores, tipografías y estilos (Material 3).
- `alarm/`: Implementación de `ReminderWorker` para las notificaciones.
- `util/`: Clases de utilidad para manejo de tiempos (`TimeUtils`) y otros procesos.

## ⚙️ Requisitos de Instalación

- Android 12.0+ (API 31) como mínimo.
- Android Studio Iguana o superior.
- Gradle 8.x.

---
Desarrollado con ❤️ para mejorar la salud cardiovascular.
