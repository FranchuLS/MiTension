# MiTensión

**MiTensión** es una aplicación de Android moderna diseñada para ayudar a los usuarios a llevar un control riguroso de su presión arterial. Permite registrar mediciones diarias, visualizar el historial en un calendario y recibir recordatorios para no olvidar las tomas.

## 🚀 Características

- **Registro de Mediciones:** Guarda fácilmente tus valores de presión sistólica y diastólica.
- **Calendario Histórico:** Visualiza tus mediciones pasadas de forma organizada por días.
- **Detalle Diario:** Consulta el desglose de las tomas realizadas en un día específico.
- **Recordatorios:** Sistema de notificaciones mediante WorkManager para asegurar la constancia en las mediciones.
- **Interfaz Moderna:** Construida íntegramente con Jetpack Compose y siguiendo las guías de Material Design 3.

## 🛠️ Tecnologías Utilizadas

- **Kotlin:** Lenguaje principal de desarrollo.
- **Jetpack Compose:** Toolkit moderno para la construcción de interfaces nativas.
- **Room Database:** Persistencia de datos local de forma robusta.
- **WorkManager:** Gestión de tareas en segundo plano para recordatorios y alarmas.
- **Navigation Compose:** Gestión de la navegación entre pantallas dentro de una Single Activity.
- **ViewModel & LiveData/StateFlow:** Arquitectura recomendada por Android para la gestión del estado de la UI.
- **KSP (Kotlin Symbol Processing):** Para una generación de código más rápida (usado con Room).

## 📂 Estructura del Proyecto

El proyecto sigue una arquitectura limpia dividida por capas:

- `data/`: Contiene las entidades de Room, el DAO, la base de datos y el Repositorio que centraliza el acceso a los datos.
- `ui/`:
    - `screens/`: Pantallas principales (Medición, Calendario, Detalle).
    - `viewmodel/`: Lógica de negocio de la UI y gestión de estados.
    - `components/`: Componentes de Compose reutilizables.
    - `theme/`: Definición de colores, tipografías y estilos (Material 3).
- `alarm/`: Implementación de `ReminderWorker` para las notificaciones.
- `util/`: Clases de utilidad para manejo de tiempos y otros procesos.

## ⚙️ Requisitos de Instalación

- Android 12.0+ (API 31) como mínimo.
- Android Studio Iguana o superior.
- Gradle 8.x.

---
Desarrollado con ❤️ para mejorar la salud cardiovascular.
