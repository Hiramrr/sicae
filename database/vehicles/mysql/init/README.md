# Scripts MySQL - dominio vehiculos

Aqui se encuentran los scripts del dominio de vehiculos.

- `001_sicaevehiculo.sql`: script de vehiculos usado por el proyecto.

El script incluye el esquema entregado por el profesor y los campos necesarios para cumplir las reglas de `VehicleService`:

- `estatus` para activar/inactivar vehiculos.
- `tiempoCreacion` y `tiempoActualizacion`.
- restriccion unica para `placa`.
- actualizacion de la vista `vehiculofullinfo`.

MySQL ejecuta los archivos de esta carpeta solo cuando el volumen se crea por primera vez. Para reiniciar desde cero:

```bash
make reset-db
make up-db
```
