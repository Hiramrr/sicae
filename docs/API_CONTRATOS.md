# Contratos API iniciales

Estos contratos son una propuesta para arrancar. El equipo debe ajustar nombres, rutas y payloads finales conforme a los scripts de BD y a las decisiones del profesor.

## Convenciones

- Todas las rutas protegidas usan header `Authorization: Bearer <token>`.
- Todas las respuestas deben usar JSON.
- Los errores deben incluir mensaje claro y causa.
- Fechas y horas sugeridas en formato ISO 8601.

## AuthService

| Accion | Metodo | Ruta | Auth | Descripcion |
| --- | --- | --- | --- | --- |
| Login | POST | `/auth/login` | No | Autentica usuario y devuelve token. |

Payload sugerido:

```json
{
  "usuario": "admin",
  "contrasena": "admin123"
}
```

## UserService

| Accion | Metodo | Ruta | Auth | Rol |
| --- | --- | --- | --- | --- |
| Registrar usuario | POST | `/users` | Si | Administrador |
| Editar usuario | PUT | `/users/{idUsuario}` | Si | Segun reglas |
| Ver perfil | GET | `/users/{idUsuario}` | Si | Usuario autenticado |
| Cambiar estatus | PATCH | `/users/{idUsuario}/status` | Si | Administrador |

## VehicleService

| Accion | Metodo | Ruta | Auth | Descripcion |
| --- | --- | --- | --- | --- |
| Buscar por usuario | GET | `/users/{idUsuario}/vehicles` | Si | Lista vehiculos asociados. |
| Registrar vehiculo | POST | `/vehicles` | Si | Crea vehiculo asociado a usuario. |
| Editar vehiculo | PUT | `/vehicles/{idVehiculo}` | Si | Actualiza vehiculo propio. |
| Cambiar estatus | PATCH | `/vehicles/{idVehiculo}/status` | Si | Activa o desactiva vehiculo propio. |

## ParkingService

| Accion | Metodo | Ruta | Auth | Descripcion |
| --- | --- | --- | --- | --- |
| Registrar entrada | POST | `/parking/movements/entry` | Si | Registra entrada y ocupa espacio. |
| Registrar salida | PATCH | `/parking/movements/{idMovimiento}/exit` | Si | Registra salida, costo y libera espacio. |
| Consultar espacios | GET | `/parking/spaces` | Si | Lista espacios disponibles/ocupados. |

## Respuestas

Exito sugerido:

```json
{
  "success": true,
  "message": "Operacion realizada correctamente",
  "data": {}
}
```

Error sugerido:

```json
{
  "success": false,
  "message": "No se pudo completar la operacion",
  "error": "Detalle de la causa"
}
```
