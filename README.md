# AplicacionMovilGestionVoluntariado

## Objetivo del Proyecto
El objetivo central es proporcionar aplicación móvil para la gestión del voluntariado de cuatrovientos. El sistema orquesta la relación entre organizaciones que publican causas sociales y voluntarios dispuestos a participar, garantizando la integridad de los datos en procesos críticos como el **Match** y el **Control de Inscripciones**.

---
## Funcionalidades Core (Actualizado)

1.  **Gestión de Identidad (Auth):**
    *   Registro y Login diferenciado por roles (`Voluntario` y `Organización`) con autenticación segura y hash de contraseñas.
    *   **Perfiles Completos:** Los voluntarios registran habilidades, intereses, disponibilidad, zona y ciclo formativo. Las organizaciones gestionan su perfil público.

2.  **Ciclo de Actividades:**
    *   Publicación de ofertas por parte de organizaciones con validación de metadatos (fechas, cupos, ODS, habilidades requeridas).
    *   **Filtrado Avanzado:** Los voluntarios pueden buscar actividades por Zona, Habilidades, Disponibilidad, Intereses y Estado.
    *   **Dashboards:** Paneles de control específicos para Administradores, Organizaciones y Voluntarios con métricas en tiempo real.

3.  **Motor de Inscripción y Matching:**
    *   Sistema de registro de voluntarios en actividades con estados: `PENDIENTE`, `CONFIRMADO`, `RECHAZADO`, `EN CURSO`, `FINALIZADO`.
    *   **Match Administrativo:** Los administradores pueden asignar manualmente voluntarios aceptados a actividades.
    *   **Control de Aforo:** Validaciones de negocio para prevenir sobrecupo.

4.  **Gestión Administrativa:**
    *   Validación de nuevos Voluntarios y Organizaciones (Aceptar/Rechazar registros).
    *   Supervisión global de todas las actividades y matches.
    *   **Interfaz Premium:** Diseño moderno con encabezados fijos, tarjetas interactivas y búsqueda optimizada.

---

## Flujo de Trabajo y Aportaciones (Git Strategy)

Para mantener la estabilidad del código, implementamos una estrategia de **Git Flow** simplificada.

### 1. Modelo de Ramas

*   **`main`**: Código productivo. Solo se toca mediante merges de versiones estables.
*   **`develop`**: Rama de integración. Aquí se fusionan todas las tareas terminadas.
*   **`feature/` / `fix/`**: Ramas efímeras para nuevas funcionalidades o correcciones.

### Guía de Instalación

*   En este caso, el único requisito es tener la herramienta de desarrollo: **AndroidStudio**.

1. **Clonar el repositorio**
    ```bash
    git clone https://github.com/Gari885/AplicacionMovilGestionVoluntariado.git
    ```
2. **Abrirlo con AndroidStudio**
3. **Arrancar la aplicación**

## Usuarios de Prueba

Para facilitar las pruebas de la aplicación, se proporcionan los siguientes usuarios predeterminados:

| Rol            | Email                           | Contraseña    |
|----------------|---------------------------------|---------------|
| **Voluntario** | `garinovoselskyyjaka@gmail.com` | `adiosBola*`  |
| **Organización**| `g@gmail.com`                   | `1234567890`  |
| **Admin**      | `admin@admin.com`               | `adminTest`   |
