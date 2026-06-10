# Guia de trabajo del equipo

## Reglas de colaboracion

- Cada integrante debe programar, probar y explicar su parte.
- No subir contrasenas reales ni dumps privados.
- Cada cambio debe quedar asociado a una rama o commit claro.
- Antes de integrar, probar el endpoint en Postman/Hoppscotch.
- Documentar cambios de rutas en `docs/API_CONTRATOS.md`.

## Division sugerida

- Integrante 1: AuthService y JWT.
- Integrante 2: UserService y PostgreSQL.
- Integrante 3: VehicleService y MySQL.
- Integrante 4: ParkingService, calculo de costos e integracion REST.
- Integrante 5, si existe: Docker, Postman, documentacion y pruebas de integracion.

## Flujo de ramas

```text
main
feature/auth-service
feature/user-service
feature/vehicle-service
feature/parking-service
feature/postman-and-docs
```

## Checklist por endpoint

- Validacion de datos obligatorios.
- Validacion de formatos.
- Validacion de duplicados.
- Validacion de permisos/rol.
- Prueba de exito en Postman.
- Prueba de error en Postman.
- Respuesta clara y consistente.
- Explicacion preparada para demo.

## Antes de la demo

- Levantar contenedores desde cero.
- Importar scripts SQL desde las carpetas `database/`.
- Probar login y guardar token.
- Ejecutar la coleccion Postman completa.
- Revisar que ningun servicio dependa directamente de otra base de datos.
- Confirmar que todos puedan explicar flujo, capas y decisiones tecnicas.
