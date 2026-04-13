\# EventosPI Android



Aplicación Android del Proyecto Integrador 2026-1.



\## Estado actual

Versión 1 funcional con:



\- Login

\- Register

\- Obtener usuario autenticado

\- Cambiar contraseña

\- Crear evento

\- Listar eventos

\- Eliminar evento

\- Actualizar evento



\## Tecnologías usadas

\- Kotlin

\- Jetpack Compose

\- Retrofit

\- OkHttp

\- Android Studio



\## Requisitos

\- Android Studio

\- Backend del proyecto corriendo en local

\- Docker Desktop para la base de datos del backend

\- Emulador Android o dispositivo físico



\## Configuración actual

La app consume el backend local usando:



\- `10.0.2.2:5160` desde el emulador Android



\## Importante

Para pruebas locales con el emulador, en el backend se debe desactivar temporalmente la redirección HTTPS en `Program.cs`:



```csharp

// app.UseHttpsRedirection();

