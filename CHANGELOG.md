# Registro de Cambios (Changelog)

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato se basa en [Keep a Changelog](https://keepachangelog.com/es/1.0.0/),
y este proyecto se adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-12-03

### Añadido (Added)

- **Creación de la Primera Versión Funcional de la Aplicación "Mi Tensión".**
- **Pantalla de Medición:**
    - Interfaz para introducir la tensión sistólica y diastólica a través de un pop-up.
    - Título dinámico que indica el período del día (Mañana, Tarde, Noche) y el número de medición actual (ej: "1/3").
    - Lógica para limitar a 3 mediciones por período, con mensajes de error si el cupo está lleno.
    - Validación de campos para asegurar que los datos son obligatorios.
- **Persistencia de Datos:**
    - Integración de la base de datos **Room** para almacenar todas las mediciones de forma persistente.
    - Implementación del patrón `Repository` para abstraer el acceso a los datos.
    - Generación de datos de prueba ("seeding") para el mes anterior, facilitando la verificación de funcionalidades.
- **Pantalla de Calendario:**
    - Vista de calendario mensual con capacidad para navegar entre meses.
    - **Visualización de "Mapa de Calor"**: Cada día muestra 3 indicadores de color que representan la media de tensión de cada período (Mañana, Tarde, Noche).
- **Pantalla de Detalle del Día:**
    - Al pulsar un día en el calendario, se muestra una lista detallada de las mediciones de ese día.
    - Los registros se agrupan por período (Mañana, Tarde, Noche).
    - Se muestra una tarjeta destacada con la **media de tensión** para cada período.
    - La tarjeta de la media cambia de color (verde, naranja, rojo) según la clasificación de la tensión basada en guías de salud (AHA).
- **Arquitectura y Calidad:**
    - Proyecto estructurado bajo la arquitectura **MVVM** (Model-View-ViewModel).
    - Implementación de **Tests Unitarios** para la lógica de negocio (ViewModels, Utils) y **Tests Instrumentados** para la base de datos (DAO) y la UI (Compose).
- **Internacionalización (i18n):**
    - Soporte para tres idiomas: **Español (castellano), Inglés y Gallego**. Todos los textos de la UI se gestionan a través de archivos de recursos.

### Cambiado (Changed)

- Refactorización de la arquitectura para asegurar que los ViewModels no accedan directamente a los recursos del framework de Android.

