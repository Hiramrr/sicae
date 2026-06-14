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

## Estado de implementacion

### SOAP - UserService (Implementado)

- **Contrato**: `contracts/soap/userValidation.xsd` — esquema XSD con operacion `validarUsuarioPorClave`
- **Endpoint**: `mx.uv.sicae.users.ws.UserValidationEndpoint` — expuesto en `POST /ws/userValidation.wsdl`
- **Config**: `WebServiceConfig` con MessageDispatcherServlet en `/ws/*`
- **Cliente**: `mx.uv.sicae.parking.client.UserServiceClientImpl` — usa WebServiceTemplate para llamar al UserService via SOAP
- **URL en Docker**: `http://user-service:8082/ws`
- **URL local**: `http://localhost:8082/ws`

### gRPC/RPC - VehicleService (Pendiente)

Contrato `.proto` y endpoint gRPC en VehicleService aun no implementados.

## Pendiente para el equipo

- Confirmar con el profesor si basta REST + SOAP, REST + gRPC o si pide SOAP + RPC + gRPC.
- Implementar contrato gRPC en VehicleService y cliente en ParkingService.
- Documentar en la presentacion que el lenguaje es Java, mientras que las tecnologias de integracion son REST, SOAP y gRPC/RPC.
