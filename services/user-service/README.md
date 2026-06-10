# UserService

Responsable del registro, edicion, consulta de perfil y cambio de estatus de usuarios.

## Tecnologia propuesta

- Java 17+
- Spring MVC / RestController
- Maven
- MyBatis
- Apache Tomcat
- PostgreSQL
- JWT para autenticacion y autorizacion
- bcrypt para cifrar contrasenas

## Responsabilidades

- Registrar usuarios solo por administradores.
- Cifrar contrasenas con bcrypt.
- Generar claves de usuario unicas con patron acordado por el equipo.
- Editar informacion permitida.
- Consultar perfil.
- Activar o desactivar usuarios sin eliminacion fisica.
- Mantener la logica organizada en Controller, Service y Repository.

## Pendiente para el equipo

- Crear el proyecto Maven/Java en esta carpeta.
- Implementar validaciones de campos obligatorios, formato de correo, duplicados y tamanos.
- Definir autorizacion por rol.
- Documentar el contrato final en `docs/API_CONTRATOS.md`.
