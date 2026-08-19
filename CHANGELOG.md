# Registro de Cambios (Changelog)

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato se basa en [Keep a Changelog](https://keepachangelog.com/es/1.0.0/), y este proyecto se adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.0] - 2026-08-19

### Añadido (Added)

- **Seguimiento de Pulso:**
    - Campo de pulso opcional en el registro de mediciones.
    - Nuevo componente visual y diálogo para la entrada de pulsaciones por minuto (ppm).
    - Icono de corazón (`ic_heart`) para representar visualmente el pulso.
- **Tabla Resumen en Calendario:**
    - Nueva tabla que muestra las mediciones reales de los últimos 8 días (Mañana y Noche).
    - Soporte para desplazamiento horizontal y vertical en la tabla resumen.
- **Día Lógico (04:00 AM):**
    - Implementación de lógica de tiempo para agrupar mediciones nocturnas (00:00 - 03:59) en el día biológico anterior.
    - Sincronización de esta lógica en todas las pantallas (Calendario, Tabla y Detalle).

### Cambiado (Changed)

- **UI/UX Optimizado:**
    - Alineación milimétrica en las tarjetas de tensión (`TensionCard`) para una lectura más limpia.
    - Panel inferior (`BottomAppBar`) y botones redimensionados (76dp / 48dp) para mejor ergonomía y centrado perfecto.
    - Pantalla de medición ahora soporta scroll para adaptarse a dispositivos de pantalla pequeña.
    - Cabeceras más compactas para maximizar el espacio útil.
- **Actualización Tecnológica:**
    - Migración a **Kotlin 2.0**.
    - Integración del nuevo **Compose Compiler Gradle plugin**.
- **Documentación:**
    - `README.md` actualizado con instrucciones de release y lógica de funcionamiento de la app.

### Corregido (Fixed)

- **Alineación Visual:** Resueltos los desajustes de iconos y textos en las tarjetas de medición.
- **Sincronización de Datos:** Corregida la discrepancia horaria entre la base de datos (UTC) y la aplicación (Local).
- **Estabilidad de Base de Datos:** Implementada migración manual (v1 -> v2) para evitar la pérdida de datos del usuario.

## [1.0.0] - 2025-12-07

### Añadido (Added)

- **Creación de la Primera Versión Funcional de "Mi Tensión".**
- ... (contenido anterior) ...
