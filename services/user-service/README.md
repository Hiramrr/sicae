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
- bcrypt para cifrar contraseñas

## Responsabilidades

- Registrar usuarios solo por administradores.
- Cifrar contraseñas con bcrypt.
- Generar claves de usuario unicas con patron acordado por el equipo.
- Editar informacion permitida.
- Consultar perfil.
- Activar o desactivar usuarios sin eliminacion fisica.
- Exponer validacion de usuario para ParkingService mediante SOAP, si el equipo usa esta tecnologia de integracion.
- Mantener la logica organizada en Controller, Service y Repository.

## Pendiente para el equipo

- Crear el proyecto Maven/Java en esta carpeta.
- Implementar validaciones de campos obligatorios, formato de correo, duplicados y tamanos.
- Definir autorizacion por rol.
- Definir contrato SOAP en `contracts/soap/`, si aplica.
- Documentar el contrato final en `docs/API_CONTRATOS.md`.
