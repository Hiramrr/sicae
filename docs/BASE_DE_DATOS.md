# Base de datos real del profesor

Scripts recibidos:

- PostgreSQL: `database/users/postgres/init/001_sicaeUsuario.sql`
- MySQL vehiculos: `database/vehicles/mysql/init/001_sicaevehiculo.sql`
- MySQL estacionamiento: `database/parking/mysql/init/001_sicaeestacionamiento.sql`

## Nombres de bases de datos

| Dominio | Motor | Nombre |
| --- | --- | --- |
| Usuarios | PostgreSQL | `sicaeUsuario` |
| Vehiculos | MySQL 8 | `sicaevehiculo` |
| Estacionamiento | MySQL 8 | `sicaeEstacionamiento` |

Estos nombres ya estan configurados directamente en `docker-compose.yml` y `deployment/*.compose.example.yml`.

## Credenciales locales de Docker

Estas credenciales son solo para el entorno escolar/local del proyecto.

| Base | Host en Docker | Puerto local | Usuario | Contrasena |
| --- | --- | --- | --- | --- |
| `sicaeUsuario` | `users-db` | `5432` | `sicae_usuario_app` | `sicae123` |
| `sicaevehiculo` | `vehicles-db` | `3307` | `sicae_vehiculo_app` | `sicae123` |
| `sicaeEstacionamiento` | `parking-db` | `3308` | `sicae_estacionamiento_app` | `sicae123` |

Para MySQL, el usuario `root` usa la contraseña `root123`.

## Usuarios

Tablas:

- `programaEducativo`
- `rol`
- `tipoUsuario`
- `usuario`

Vista:

- `usuarioFullInfo`

Columnas principales de `usuario`:

- `idUsuario`
- `nombre`
- `apellidoPaterno`
- `apellidoMaterno`
- `claveUsuario`
- `email`
- `telefono`
- `username`
- `password`
- `estatus`
- `idRol`
- `idTipoUsuario`
- `idProgramaEducativo`
- `tiempoCreacion`
- `tempoActualizacion`

Datos semilla importantes:

- Usuario administrador: `admin`
- Email: `admin@example.com`
- Rol: `administrador`
- Clave de usuario: `fei`
- La contraseña viene como hash bcrypt en el script. Confirmen con el profesor la contraseña en texto claro para las pruebas de login.

## Vehiculos

Tablas:

- `marca`
- `modelo`
- `vehiculo`

Vista:

- `vehiculofullinfo`

Columnas principales de `vehiculo`:

- `idVehiculo`
- `idUsuario`
- `claveVehiculo`
- `idModelo`
- `placa`
- `color`
- `anio`
- `descripcion`
- `estatus`
- `tiempoCreacion`
- `tiempoActualizacion`

Restricciones importantes:

- `placa` debe ser unica.
- El script de vehiculos incluye los campos necesarios para activar/inactivar vehiculos y registrar tiempos de creacion/actualizacion.

## Estacionamiento

Tablas:

- `espacioestacionamiento`
- `movimiento`

Vista:

- `movimientofullinfo`

Columnas principales de `espacioestacionamiento`:

- `idEspacio`
- `claveEspacio`
- `tipo`
- `ocupado`
- `estatus`

Columnas principales de `movimiento`:

- `idMovimiento`
- `idVehiculo`
- `tiempoEntrada`
- `tiempoSalida`
- `minutosEstacionado`
- `horasCobradas`
- `costoTotal`
- `tarifaHora`
- `tiempoCreacion`
- `tiempoActualizacion`
- `idEspacio`

Observaciones importantes:

- El PDF plantea registrar entrada primero y salida despues, pero el script marca `tiempoSalida` como `NOT NULL`. Confirmen si al registrar entrada deben guardar un valor temporal o si deben ajustar el esquema.
- La vista `movimientofullinfo` del script usa una condicion de union que conviene revisar durante las pruebas, porque podria devolver mas filas de las esperadas.

## Verificacion local

Despues de levantar las bases:

```bash
make up-db
make verify-db
```

Si ya habian levantado los contenedores antes de copiar los scripts, reinicien volumenes para que Docker vuelva a ejecutar los SQL:

```bash
make reset-db
make up-db
make verify-db
```
