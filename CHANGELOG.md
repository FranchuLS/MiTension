# Registro de Cambios (Changelog)

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato se basa en [Keep a Changelog](https://keepachangelog.com/es/1.0.0/), y este proyecto se adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-12-07

### Añadido (Added)

- **Creación de la Primera Versión Funcional de "Mi Tensión".**
- **Arquitectura y Configuración:**
    - Proyecto inicializado en Kotlin con Jetpack Compose y arquitectura MVVM.
    - Integración de la base de datos **Room** para persistencia de datos con patrón Repositorio.
    - Configuración de tests unitarios (`JUnit`, `MockK`, `Turbine`) y tests instrumentados (`Compose Test Rule`).
    - Soporte para internacionalización (i18n) con textos en **Español, Inglés y Gallego**.

- **Pantalla de Medición:**
    - Interfaz para introducir la tensión sistólica y diastólica.
    - Título dinámico que indica el período del día (Mañana, Tarde, Noche) y el progreso de las mediciones (ej: "Medición 1/3").
    - Lógica de negocio para limitar a 3 mediciones por período. Si el cupo está lleno, se notifica al usuario y se impide guardar más registros.
    - Validación de campos para asegurar que ambos valores son obligatorios antes de guardar.

- **Pantalla de Calendario:**
    - Vista de calendario mensual con navegación para cambiar de mes y año.
    - **Visualización de "Mapa de Calor"**: Cada día en el calendario muestra 3 indicadores de color, representando la media de tensión de cada período (Mañana, Tarde, Noche), permitiendo una evaluación visual rápida.

- **Pantalla de Detalle del Día:**
    - Muestra una lista detallada de las mediciones de un día específico, agrupadas por período.
    - Presenta una tarjeta destacada con la **media de tensión** para cada período.
    - La tarjeta de la media cambia de color (verde, naranja, rojo, etc.) según la clasificación de la tensión basada en guías de salud, ofreciendo feedback visual inmediato.

- **Recordatorios y Notificaciones:**
    - Implementación de **WorkManager** para planificar recordatorios periódicos y fiables.
    - Un `Worker` comprueba en segundo plano si el usuario ha completado sus mediciones para el período actual.
    - Si faltan mediciones, se envía una notificación al usuario con un icono personalizado.
    - Se solicita el permiso de notificaciones en Android 13+ para cumplir con las políticas del sistema.

### Cambiado (Changed)

- **UI/UX:**
    - Se ha ajustado el tamaño de la fuente en los botones para mejorar la visualización en idiomas con textos largos.
    - Se ha simplificado la vista de detalle, mostrando "Alta" y "Baja" en lugar de las etiquetas completas para una interfaz más limpia.
- **Arquitectura:**
    - Refactorizada la comunicación UI-ViewModel para que los ViewModels no accedan directamente a recursos de Android (`R.string`, etc.), mejorando la capacidad de testeo y la separación de responsabilidades.
- **Base de Datos:**
    - Implementado un `Callback` de Room para poblar la base de datos con datos de prueba ("seeding") la primera vez que se crea, facilitando el desarrollo y la verificación de la UI.

### Corregido (Fixed)

- Resueltos múltiples problemas de configuración de Gradle relacionados con la incompatibilidad de versiones entre `Kotlin`, `KSP` y el compilador de `Compose`.
- Solucionados errores de compilación en los archivos de recursos `strings.xml` debidos a una sintaxis XML incorrecta.
- Corregidos los tests unitarios e instrumentados para asegurar su correcto funcionamiento y fiabilidad.

