# VehicleService

Responsable de busqueda, registro, edicion y cambio de estatus de vehiculos.

## Tecnologia propuesta

- Node.js LTS
- Express
- TypeScript
- mysql2
- JWT para autenticacion
- MySQL 8

## Responsabilidades

- Buscar vehiculos de un usuario.
- Registrar vehiculos sin placas duplicadas.
- Limitar a 4 vehiculos activos por usuario.
- Editar solo vehiculos asociados al usuario autenticado.
- Activar o desactivar vehiculos sin eliminacion fisica.
- Mantener la logica organizada en controllers, services y repositories.

## Pendiente para el equipo

- Crear `package.json`, configuracion TypeScript y servidor Express.
- Implementar middleware JWT.
- Implementar repositorios MySQL.
- Documentar el contrato final en `docs/API_CONTRATOS.md`.
