# AuthService

Responsable del login y emision de JWT.

## Tecnologia propuesta

- Java 17+
- Spring MVC / RestController
- Maven
- MyBatis
- Apache Tomcat
- PostgreSQL, usando la base del dominio de usuarios
- bcrypt para verificar contrasenas
- JWT para emitir tokens

## Responsabilidades

- Validar usuario y contrasena.
- Permitir login solo a usuarios registrados y activos.
- Devolver datos del usuario autenticado y token JWT.
- Mantener la logica organizada en Controller, Service y Repository.

## Pendiente para el equipo

- Crear el proyecto Maven/Java en esta carpeta.
- Definir modelos, mappers MyBatis y configuracion de seguridad.
- Implementar el endpoint de login.
- Documentar el contrato final en `docs/API_CONTRATOS.md`.
