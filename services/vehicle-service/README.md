# VehiculoService

Microservicio REST + JSON para administrar vehiculos del sistema SICAE.

## Tecnologia

- Java 17+
- Spring Boot / RestController
- Maven
- MyBatis
- MySQL 8
- Tomcat embebido de Spring Boot

## Responsabilidades

- Buscar vehiculos de un usuario.
- Registrar vehiculos sin placas duplicadas.
- Limitar a 4 vehiculos activos por usuario.
- Editar solo vehiculos asociados al usuario autenticado.
- Activar o desactivar vehiculos sin eliminacion fisica.
- Mantener la logica organizada en Controller, Service y Repository.

## Endpoints

| Accion | Metodo | Ruta |
| --- | --- | --- |
| Buscar vehiculos de usuario | GET | `/api/vehiculos/usuario/{idUsuario}` |
| Registrar vehiculo | POST | `/api/vehiculos/registrar` |
| Editar vehiculo | PUT | `/api/vehiculos/editar/{idVehiculo}` |
| Cambiar estatus | PATCH | `/api/vehiculos/estatus/{idVehiculo}` |

## Ejecucion local con IntelliJ IDEA

1. Levanta la base de datos de vehiculos desde la raiz del proyecto:

```bash
docker compose up -d vehicles-db
```

2. Abre en IntelliJ la carpeta:

```text
services/vehicle-service
```

3. Espera a que IntelliJ importe Maven.
4. Ejecuta la clase:

```text
mx.uv.sicae.vehicle.VehiculoServiceApplication
```

El servicio queda en:

```text
http://localhost:8083
```

Tambien se puede compilar desde esta carpeta con Maven Wrapper:

```bash
./mvnw.cmd -DskipTests package
```

## Autenticacion

Primero inicia sesion en `AuthService`:

```text
POST http://localhost:8081/auth/login
```

Luego consume los endpoints de vehiculos con el token JWT:

```text
Authorization: Bearer <token>
```

## Ejemplo para registrar vehiculo

```json
{
  "idUsuario": 1,
  "idModelo": 1,
  "placa": "ABC1234",
  "color": "Blanco",
  "anio": 2020,
  "descripcion": "Vehiculo principal"
}
```
