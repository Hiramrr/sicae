# VehicleService

Responsable de busqueda, registro, edicion y cambio de estatus de vehiculos.

## Tecnologia propuesta

- Java 17+
- Spring MVC / RestController
- Maven
- MyBatis
- Apache Tomcat
- JWT para autenticacion
- MySQL 8

## Responsabilidades

- Buscar vehiculos de un usuario.
- Registrar vehiculos sin placas duplicadas.
- Limitar a 4 vehiculos activos por usuario.
- Editar solo vehiculos asociados al usuario autenticado.
- Activar o desactivar vehiculos sin eliminacion fisica.
- Exponer validacion de placa/asociacion para ParkingService mediante gRPC o RPC, si el equipo usa esta tecnologia de integracion.
- Mantener la logica organizada en Controller, Service y Repository.

## Pendiente para el equipo

- Crear el proyecto Maven/Java en esta carpeta.
- Implementar filtro o interceptor JWT.
- Implementar mappers/repositorios MyBatis para MySQL.
- Definir contrato gRPC/RPC en `contracts/grpc/` o `contracts/rpc/`, si aplica.
- Documentar el contrato final en `docs/API_CONTRATOS.md`.
