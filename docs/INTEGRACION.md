# Tecnologias de integracion

El proyecto se programara en Java para todos los microservicios. La variedad tecnologica se cubre en la comunicacion entre servicios, usando tecnologias vistas en clase como SOAP, RPC o gRPC.

## Criterio recomendado

- API publica para pruebas con Postman/Hoppscotch: REST + JSON.
- Comunicacion interna entre microservicios: usar al menos una tecnologia adicional de integracion.

Propuesta para el equipo:

| Flujo | Tecnologia | Responsable |
| --- | --- | --- |
| Cliente/Postman -> AuthService, UserService, VehicleService, ParkingService | REST + JSON | Todos |
| ParkingService -> UserService, validar usuario activo por `claveUsuario` | SOAP | UserService y ParkingService |
| ParkingService -> VehicleService, validar placa y asociacion con usuario | gRPC/RPC | VehicleService y ParkingService |

Con esto todos siguen usando Java, pero el proyecto demuestra integracion heterogenea entre servicios.

## Por que asi

- REST se conserva porque el PDF pide APIs REST y porque facilita las pruebas con Postman.
- SOAP permite demostrar contratos formales tipo WSDL.
- gRPC permite demostrar RPC moderno con contrato `.proto`.
- ParkingService es el mejor punto de integracion porque necesita validar usuario y vehiculo antes de registrar entradas y salidas.

## Contratos sugeridos

### SOAP - UserService

Operacion sugerida:

```text
validarUsuarioPorClave(claveUsuario) -> UsuarioValidado
```

Respuesta minima:

```text
idUsuario
claveUsuario
nombreCompleto
activo
rol
tipoUsuario
```

### gRPC/RPC - VehicleService

Servicio sugerido:

```text
VehicleValidationService
```

Operacion sugerida:

```text
ValidateVehicleByPlate(claveUsuario, placa) -> VehicleValidationResponse
```

Respuesta minima:

```text
idVehiculo
idUsuario
placa
activo
perteneceAlUsuario
mensaje
```

## Pendiente para el equipo

- Confirmar con el profesor si basta REST + SOAP, REST + gRPC o si pide SOAP + RPC + gRPC.
- Definir los contratos reales en `contracts/`.
- Implementar clientes internos en ParkingService.
- Documentar en la presentacion que el lenguaje es Java, mientras que las tecnologias de integracion son REST, SOAP y gRPC/RPC.
