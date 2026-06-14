# Contratos SOAP

## Servicio: UserValidationService

Permite que ParkingService valide que un usuario existe y esta activo sin conectarse directamente a la base de datos de usuarios.

### Contrato formal

`contracts/soap/userValidation.xsd` — Esquema XSD que define:

**Operacion:**
```
validarUsuarioPorClave(claveUsuario) -> UsuarioValidado
```

**Entrada (validarUsuarioPorClaveRequest):**
| Campo | Tipo | Descripcion |
|-------|------|-------------|
| claveUsuario | string | Clave unica del usuario (ej: RGR-254) |

**Salida (validarUsuarioPorClaveResponse):**
| Campo | Tipo | Descripcion |
|-------|------|-------------|
| idUsuario | int | ID del usuario |
| claveUsuario | string | Clave del usuario |
| nombreCompleto | string | Nombre completo del usuario |
| activo | boolean | Indica si el usuario esta activo |
| rol | string | Rol del usuario (Administrador, Invitado) |
| tipoUsuario | string | Tipo de usuario (Docente, Administrativo, Estudiante) |

### Implementacion

- **Server (UserService):** `mx.uv.sicae.users.ws.UserValidationEndpoint`
  - Expuesto en: `POST /ws/userValidation.wsdl` (puerto 8082)
- **Client (ParkingService):** `mx.uv.sicae.parking.client.UserServiceClientImpl`
  - Usa `WebServiceTemplate` para llamar al endpoint SOAP
  - URL en Docker: `http://user-service:8082/ws`
  - URL local: `http://localhost:8082/ws`
